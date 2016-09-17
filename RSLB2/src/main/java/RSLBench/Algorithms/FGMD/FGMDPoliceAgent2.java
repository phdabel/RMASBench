package RSLBench.Algorithms.FGMD;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import RSLBench.Constants;
import RSLBench.Algorithms.BMS.BinaryMaxSumMessage;
import RSLBench.Algorithms.BMS.NodeID;
import RSLBench.Algorithms.BMS.RSLBenchCommunicationAdapter;
import RSLBench.Algorithms.BMS.factor.BMSAtMostOneFactor;
import RSLBench.Algorithms.BMS.factor.BMSCardinalityFactor;
import RSLBench.Assignment.Assignment;
import RSLBench.Assignment.DCOP.DCOPAgent;
import RSLBench.Comm.CommunicationLayer;
import RSLBench.Comm.Message;
import RSLBench.Helpers.Utility.ProblemDefinition;
import es.csic.iiia.bms.Factor;
import es.csic.iiia.bms.MaxOperator;
import es.csic.iiia.bms.Minimize;
import es.csic.iiia.bms.factors.CardinalityFactor;
import es.csic.iiia.bms.factors.WeightingFactor;
import rescuecore2.config.Config;
import rescuecore2.worldmodel.EntityID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FGMDPoliceAgent2 implements DCOPAgent {

	private static final Logger Logger = LogManager.getLogger(FGMDPoliceAgent2.class);
	
	private static final MaxOperator MIN_OPERATOR = new Minimize();
	
	private double BLOCKED_PENALTY;
	private double POLICE_ETA;
	
	private EntityID id;
	private ProblemDefinition problem;
	private BMSAtMostOneFactor<NodeID> variableNode;
	private HashMap<NodeID, Factor<NodeID>> factors;
	private HashMap<NodeID, EntityID> factorLocations;
	private RSLBenchCommunicationAdapter communicationAdapter;
	private EntityID targetId;
	private long constraintChecks;
	
	@Override
	public void initialize(Config config, EntityID agentID, ProblemDefinition problem) {
		Logger.trace("Initializing agent {}", agentID);

        BLOCKED_PENALTY = problem.getConfig().getFloatValue(
                Constants.KEY_BLOCKED_FIRE_PENALTY);
        POLICE_ETA = problem.getConfig().getFloatValue(Constants.KEY_POLICE_ETA);

        this.id = agentID;
        this.targetId = null;
        this.problem = problem;

        // Reset internal structures
        factors = new HashMap<>();
        factorLocations = new HashMap<>();
        communicationAdapter = new RSLBenchCommunicationAdapter(config);

        // Build the variable node
        addPoliceFactor();

        // And the blockade factor nodes that correspond to this agent
        addBlockadeFactors();

        // Finally, compute the location of each factor in the simulation
        computeFactorLocations();

        Logger.trace("Agent {} initialized.", agentID);

	}
	
	/**
	 * adds a new factor to this agent 
	 * 
	 * @param id
	 * @param factor
	 */
	private void addFactor(NodeID id, Factor<NodeID> factor){
		factors.put(id, factor);
		factor.setMaxOperator(MIN_OPERATOR);
		factor.setIdentity(id);
		factor.setCommunicationAdapter(communicationAdapter);
	}
	
	private void addPoliceFactor(){
		this.variableNode = new BMSAtMostOneFactor<>();
		
		// the agent's factor is the selector plus the independent utilities
		// of this agent for each blockade
		WeightingFactor<NodeID> agentFactor = new WeightingFactor<>(variableNode);
		
		for(EntityID blockade: problem.getBlockades()){
			NodeID blockadeID = new NodeID(null, blockade);
			//link the agent to each blockade
			agentFactor.addNeighbor(blockadeID);
			
			// ... and populate the utilities
			double value = problem.getPoliceUtility(id, blockade);
			if(problem.isPoliceAgentBlocked(id, blockade)){
				value += BLOCKED_PENALTY;
			}
			agentFactor.setPotential(blockadeID, value);
			
			Logger.trace("Utility for {}: {}", new Object[]{blockade, value});
		}
		addFactor(new NodeID(id, null), agentFactor);
	}
	
	/**
	 * create the factor nodes of the blockades controlled by this agent.
	 * 
	 * blockade factors are assigned to the police agents according to their
	 * indices within the utilities list of polices brigades and blockades.
	 * 
	 * agent i gets all blockades f s.t. f mod len(agents) == i
	 * if there are 2 police agents and 5 blockade functions, the assignment
	 * goes like that:
	 * agent 0 (agents.get(0)) gets blockades 0,2,4
	 * agent 1 (agents.get(1)) gets blockades 1,3
	 * 
	 */
	private void addBlockadeFactors()
	{
		ArrayList<EntityID> agents = problem.getPoliceAgents();
		ArrayList<EntityID> blockades = problem.getBlockades();
		final int nAgents = agents.size();
		final int nBlockades = blockades.size();
		final int nAgent = agents.indexOf(id);
		
		//iterate over the blockades whose factors must run within this agent
		for(int i = nAgent; i < nBlockades; i += nAgents){
			final EntityID blockade = blockades.get(i);
			
			//build the factor node
			BMSCardinalityFactor<NodeID> f = new BMSCardinalityFactor<>();
			f.setFunction(new CardinalityFactor.CardinalityFunction() {
				@Override
				public double getCost(int i){
					return (i>0) ? POLICE_ETA : 0;
				}
			});
			
			//link the blockade with all agents
			for(EntityID agent : agents){
				f.addNeighbor(new NodeID(agent, null));
			}
			
			//finally add the factor to this agent
			addFactor(new NodeID(null, blockade),f);
		}
	}
	
	
	/**
	 *  creates a map of factor id to the agent id where this factor is running,
	 *  for all factors related to the police team.
	 *  
	 *  @see #addBlockadeFactors() for information on how the logical factors are assigned to the agents.
	 */
	private void computeFactorLocations()
	{
		ArrayList<EntityID> agents = problem.getPoliceAgents();
		ArrayList<EntityID> blockades = problem.getBlockades();
		final int nAgents = agents.size();
		final int nBlockades = blockades.size();
		
		
		//easy part: each agent selector runs on the corresponding agent
		for(EntityID agent : agents){
			factorLocations.put(new NodeID(agent, null), agent);
		}
		
		//harder part: each blockade f runs on agent f mod len(agents)
		for(int i = 0; i < nBlockades; i++){
			EntityID agent = agents.get(i % nAgents);
			EntityID blockade = blockades.get(i);
			factorLocations.put(new NodeID(null,blockade),agent);
		}
	}

	/**
	 * tries to improve the current assignment given the received messages.
	 * <p/>
	 * in binary max-sum this amounts to run each factor within this agent,
	 * and them extracting the best current assignment from the selector of the agent.
	 */
	@Override
	public boolean improveAssignment() {
		Logger.trace("improveAssignment start...");
		constraintChecks = 0;
		
		//let all factor run
		for(NodeID eid : factors.keySet()){
			constraintChecks += factors.get(eid).run();
		}
		
		//now extract our choice
		NodeID target = variableNode.select();
		if(target == null || target.target == null){
			Logger.debug("Agent {} chose no target!",id);
			targetId = Assignment.UNKNOWN_TARGET_ID;
		}else{
			Logger.debug("Agent {} chooses target {}",id,targetId);
			targetId = target.target;
		}
		Logger.trace("improceAssignment end.");
		
		return !communicationAdapter.isConverged();
		
	}

	@Override
	public EntityID getID() {
		return this.id;
	}

	@Override
	public EntityID getTarget() {
		return targetId;
	}

	@Override
	public Collection<BinaryMaxSumMessage> sendMessages(CommunicationLayer com) {
		//fetch the messages that must be sent
		Collection<BinaryMaxSumMessage> messages = communicationAdapter.flushMessages();
		
		//send them
		for(BinaryMaxSumMessage message: messages){
			EntityID recipientAgent = factorLocations.get(message.getRecipientFactor());
			com.send(recipientAgent, message);
		}
		
		return messages;
	}

	/**
	 * receives a set of messages from other agents, by dispatching them to their intended recipient factors
	 * @param messages messages to receive
	 */
	@Override
	public void receiveMessages(Collection<Message> messages) {
		if(messages == null){
			return;
		}
		
		for(Message amessage : messages){
			if(amessage == null){
				continue;
			}
			receiveMessage(amessage);
		}

	}
	
	/**
	 * receives a single message from another agent, dispatching it to the intended recipient factor
	 * @param amessage message to receive
	 */
	private void receiveMessage(Message amessage){
		if(!(amessage instanceof BinaryMaxSumMessage)){
			throw new IllegalArgumentException("Binary max-sum agents are only supposed to receive binary max-sum messages.");
		}
		BinaryMaxSumMessage message = (BinaryMaxSumMessage)amessage;
		Factor<NodeID> recipient = factors.get(message.getRecipientFactor());
		recipient.receive(message.message, message.getSenderFactor());
	}

	@Override
	public long getConstraintChecks() {
		return constraintChecks;
	}

}
