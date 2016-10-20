package RSLBench.Algorithms.FGMD;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import RSLBench.Algorithms.BMS.factor.BMSSelectorFactor;
import RSLBench.Algorithms.BMS.BinaryMaxSumMessage;
import RSLBench.Algorithms.BMS.NodeID;
import RSLBench.Algorithms.BMS.RSLBenchCommunicationAdapter;
import RSLBench.Algorithms.BMS.factor.BMSCardinalityFactor;
import java.util.Collection;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import rescuecore2.worldmodel.EntityID;
import RSLBench.Assignment.DCOP.DCOPAgent;
import RSLBench.Comm.Message;
import RSLBench.Helpers.Utility.ProblemDefinition;
import es.csic.iiia.bms.Factor;
import es.csic.iiia.bms.MaxOperator;
import es.csic.iiia.bms.Minimize;
import es.csic.iiia.bms.factors.CardinalityFactor.CardinalityFunction;
import es.csic.iiia.bms.factors.WeightingFactor;
import RSLBench.Comm.CommunicationLayer;
import RSLBench.Constants;
import RSLBench.Algorithms.FGMD.FGMDProblemDefinition;

import rescuecore2.config.Config;


public class FGMDFireAgent implements DCOPAgent {

	private static final Logger Logger = LogManager.getLogger(FGMDFireAgent.class);
	
	private static final MaxOperator MIN_OPERATOR = new Minimize();
	
	private double w_j;
	
	private double selfish_penalty;
	//private final double w_j = 2.0;
	
	private EntityID id;
	private ProblemDefinition problem;
	private BMSSelectorFactor<NodeID> variableNode;
	private HashMap<NodeID, Factor<NodeID>> factors;
	private HashMap<NodeID, EntityID> factorLocations;
	private RSLBenchCommunicationAdapter communicationAdapter;
	private EntityID targetId;
	private long constraintChecks;
	
	/**
	 *  initialize the firefighter agent
	 *  
	 *  @param agentID the platform ID of the firefighting team
	 *  @param problem a utility matrix that contains <em>all</em> <code>\delta_ij</code> values
	 */
	@Override
	public void initialize(Config config, EntityID agentID, ProblemDefinition problem) {
		Logger.trace("Initializing agent {}", agentID);
		
		this.id = agentID;
		this.targetId = null;
		this.problem = problem;
		this.w_j = config.getFloatValue(FGMDBinaryMaxSum.FGMD_WORKLOAD);
		this.selfish_penalty = config.getFloatValue(FGMDBinaryMaxSum.FGMD_SELFISH_PENALTY);
		//reset internal structures
		factors = new HashMap<>();
		factorLocations = new HashMap<>();
		communicationAdapter = new RSLBenchCommunicationAdapter(config);
		
		// build the selector node for each agent
		this.addSelectorNode();
		
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
	
	/**
	 * creates a selector node for the agent's variable.
	 */
	private void addSelectorNode()
	{
		this.variableNode = new BMSSelectorFactor<>();
		
		/**
		 * the agent's factor is the selector factor plus the independent utilities
		 * of this agent for each fire
		 */
		WeightingFactor<NodeID> agentFactor = new WeightingFactor<>(variableNode);
		
		for(EntityID fire : problem.getFireAgentNeighbors(id)){
			NodeID fireID = new NodeID(null, fire);
			//link the agent to each fire
			agentFactor.addNeighbor(fireID);
			
			double delta_ij = problem.getFireUtility(id, fire);
			double max_delta = problem.getFireUtility(id, problem.getHighestTargetForFireAgent(id));
			
			 
            /* modification of the problem for FGMD */
            if(problem.isFireAgentBlocked(id, fire)){
            	delta_ij += problem.getConfig().getFloatValue(Constants.KEY_BLOCKED_FIRE_PENALTY);
            }
            /* end of the modification of the problem for FGMD */
			
			//and populate the utilities
			double value = -delta_ij * Math.exp((-delta_ij/max_delta));
			
			agentFactor.setPotential(fireID, value);
			Logger.trace("Utility for {}: {}", new Object[]{fire,value});
		}
		addFactor(new NodeID(id, null), agentFactor);
	}
	
	/**
	 *  create the utility nodes of the fires controller by this agents.
	 *  
	 *  utility functions get assigned to the agents according to their
	 *  indices within the utilities list of agents and targets.
	 *  
	 *  agent i gets all fires f s.t. f mod len(agents) == i
	 *  if there are 2 agents and 5 utility functions, the assignment goes
	 *  like that:
	 *  
	 *  agent 0 (agents.get(0)) gets fires 0, 2, 4
	 *  agent 1 (agents.get(1)) gets fires 1, 3
	 *  
	 */
	private void addUtilityNodes()
	{
		ArrayList<EntityID> fires = problem.getFires();
		final int nAgents = problem.getNumFireAgents();
		final int nFires = fires.size();
		final int nAgent = problem.getFireAgents().indexOf(id);
		
		// iterate over the fires whose utility functions must run within this agent
		for(int i = nAgent; i < nFires; i += nAgents){
			final EntityID fire = fires.get(i);
			final NodeID fireID = new NodeID(null, fire);
			
			//build the utility node
			BMSCardinalityFactor<NodeID> f = new BMSCardinalityFactor<>();
			
			// set the maximum number of agents that should be attending this fire
			CardinalityFunction wf = new CardinalityFunction(){
				@Override
				public double getCost(int nActiveVariables){
					if(nActiveVariables < w_j){
						return selfish_penalty;
					}else{
						return (w_j / (nActiveVariables * (nActiveVariables - (w_j - 1))));
					}
				}
			};
			
			f.setFunction(wf);
			
			//link the fire with all its neighboring agents
			for(EntityID agent : problem.getFireNeighbors(fire)){
				f.addNeighbor(new NodeID(agent, null));
			}
			
			//finally add the factor to this agent
			addFactor(fireID, f);
		}
	}
	
	/**
	 * creates a map of factor id to the agent id where this factor is running,
	 * for all factors within the simulation.
	 * 
	 * @see #addUtilityNodes() for information on how the logical factors are assigned to agents.
	 */
	private void computeFactorLocations()
	{
		ArrayList<EntityID> agents = problem.getFireAgents();
		ArrayList<EntityID> fires = problem.getFires();
		final int nAgents = agents.size();
		final int nFires = fires.size();
		
		//easy part: each agent selector runs on the corresponding agent
		for(EntityID agent : agents)
		{
			factorLocations.put(new NodeID(agent,null), agent);
		}
		
		// harder part: each fire f runs on agent f mod len(agents)
		for(int i = 0; i < nFires; i++){
			EntityID agent = agents.get(i % nAgents);
			EntityID fire = fires.get(i);
			factorLocations.put(new NodeID(null, fire), agent);
		}
	}

	/**
	 * tries to improve the current assignment given the received messages.
	 * <p/>
	 * In binary max-sum this amounts to run each factor within this agent,
	 * and then extracting the best current assignment from the selector of the
	 * agent.
	 */
	@Override
	public boolean improveAssignment() {
		Logger.trace("improveAssignment start...");
		constraintChecks = 0;
		
		//let all factors run
		for(NodeID eid : factors.keySet()){
			constraintChecks += factors.get(eid).run();
		}
		
		//now extract our choice
		final List<EntityID> candidateFires = problem.getFireAgentNeighbors(id);
		if(candidateFires.isEmpty()){
			//if the agent has no candidate fires just send her to the nearest fire
			targetId = problem.getHighestTargetForFireAgent(id);
			return false;
		}
		
		NodeID target = variableNode.select();
		if(target == null || target.target == null){
			//if it has candidates but chose none, this is an error
			Logger.error("Agent {} chose no target! Candidates: {}", id, problem.getFireAgentNeighbors(id));
			System.exit(1);
		}else{
			//otherwise, assign it to the chosen target
			targetId = target.target;
		}
		Logger.trace("improveAssignment end.");
		
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
	public Collection<? extends Message> sendMessages(CommunicationLayer com) {
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
	 * receives a set of messages from other agents, by dispatching them to their
	 * intended recipient factors.
	 * 
	 * @param messages messages to receive
	 */
	@Override
	public void receiveMessages(Collection<Message> messages) {
		if(messages == null)
			return;
		for(Message amessage : messages){
			if(amessage == null){
				continue;
			}
			receiveMessage(amessage);
		}
	}
	
	/**
	 * receives a single message from another agent, dispatching it to the
	 * intended recipient factor
	 * 
	 * @param amessage message to receive
	 */
	private void receiveMessage(Message amessage)
	{
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
