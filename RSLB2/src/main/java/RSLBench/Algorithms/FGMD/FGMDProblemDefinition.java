package RSLBench.Algorithms.FGMD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import RSLBench.Constants;
import RSLBench.Assignment.Assignment;
import RSLBench.Helpers.PathCache.PathDB;
import RSLBench.Helpers.Utility.ProblemDefinition;
import RSLBench.Helpers.Utility.UtilityFactory;
import RSLBench.Helpers.Utility.UtilityFunction;
import RSLBench.Search.SearchResults;
import rescuecore2.config.Config;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.Blockade;
import rescuecore2.standard.entities.Human;
import rescuecore2.standard.entities.StandardWorldModel;
import rescuecore2.worldmodel.EntityID;

/**
 * This class represents the current world status as utilities.
 *
 * Utilities are calculated using the configured UtilityFunction.
 */
public class FGMDProblemDefinition extends ProblemDefinition {

	private static final Logger Logger = LogManager.getLogger(FGMDProblemDefinition.class);
	
	public FGMDProblemDefinition(Config config, ArrayList<EntityID> fireAgents, ArrayList<EntityID> fires,
			ArrayList<EntityID> policeAgents, ArrayList<EntityID> blockades, Assignment lastAssignment,
			StandardWorldModel world) {
		super(config, fireAgents, fires, policeAgents, blockades, lastAssignment, world); 
	}
    
}
