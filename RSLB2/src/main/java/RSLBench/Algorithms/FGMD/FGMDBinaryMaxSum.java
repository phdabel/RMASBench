package RSLBench.Algorithms.FGMD;

import RSLBench.Constants;
import RSLBench.Assignment.DCOP.DCOPAgent;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import RSLBench.Assignment.DCOP.DCOPSolver;
import rescuecore2.standard.entities.StandardEntityURN;

/**
 * 
 * @author Abel Correa <abel.correa@inf.ufrgs.br>
 *
 */
public class FGMDBinaryMaxSum extends DCOPSolver {

	private static final Logger Logger = LogManager.getLogger(FGMDBinaryMaxSum.class);
	
	/**
	 *  The damping factor to employ
	 */
	public static final String KEY_MAXSUM_DAMPING = "maxsum.damping";
	public static final String FGMD_WORKLOAD = "fgmd.workload";
	public static final String FGMD_SELFISH_PENALTY = "fgmd.selfish_penalty";
	
	@Override
	protected DCOPAgent buildAgent(StandardEntityURN type) {
		final boolean team = config.getBooleanValue(Constants.KEY_INTERTEAM_COORDINATION);
		
		switch(type){
			case FIRE_BRIGADE:
				return new FGMDFireAgent();
			case POLICE_FORCE:
				return new FGMDPoliceAgent();
			default:
				throw new UnsupportedOperationException("Agents of type " + type + " are not supported by this solver.");
		}
	}
	
	@Override
	public String getIdentifier() {
		return "FGMD";
	}
	
	@Override
	public List<String> getUsedConfigurationKeys(){
		List<String> result = super.getUsedConfigurationKeys();
		result.add(KEY_MAXSUM_DAMPING);
		result.add(FGMD_WORKLOAD);
		result.add(FGMD_SELFISH_PENALTY);
		return result;
	}


}
