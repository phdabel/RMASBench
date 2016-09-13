/*
 * Software License Agreement (BSD License)
 *
 * Copyright 2013 Marc Pujol <mpujol@iiia.csic.es>.
 *
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 *
 *   Redistributions of source code must retain the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer in the documentation and/or other
 *   materials provided with the distribution.
 *
 *   Neither the name of IIIA-CSIC, Artificial Intelligence Research Institute
 *   nor the names of its contributors may be used to
 *   endorse or promote products derived from this
 *   software without specific prior written permission of
 *   IIIA-CSIC, Artificial Intelligence Research Institute
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package es.csic.iiia.bms.factors;

import es.csic.iiia.bms.*;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Skeletal implementation of a test class that checks a THOP's computed messages against the
 * messages computed in the standard manner.
 *
 * @see es.csic.iiia.bms.factors.StandardFactor
 * @author Marc Pujol <mpujol@iiia.csic.es>
 */
@SuppressWarnings({"unchecked","rawtypes"})
public abstract class CrossFactorTestAbstract {
    private static final Logger LOG = Logger.getLogger(CrossFactorTestAbstract.class.getName());

    /** Number of tests to perform */
    public final int NUMBER_OF_RUNS = 1000;

    /** Maximum number of neighbors (variables) of the tested factors */
    public final int MAX_NEIGHBORS = 10;

    /** Generator of random values */
    private final Random generator = new Random();

    /**
     * Builds the factors to cross-test.
     * <p/>
     * The output must be an array of exactly two factors: (1) the specific HOP to test; and
     * (2) a {@link es.csic.iiia.bms.factors.StandardFactor} whose potential represents the same function.
     * <p/>
     * You <strong>must</strong> initialize the factors with the given maximization operator, and
     * {@link #link(es.csic.iiia.bms.Factor, es.csic.iiia.bms.Factor[])} them to all the
     * given neighbors.
     *
     * @param op Maximization operator to use
     * @param neighbors List of neighbors of the new factor
     * @return pair of factors to cross-test
     */
    public abstract Factor[] buildFactors(MaxOperator op, Factor[] neighbors);

    @Test
    public void testUninitializedFactorMessagesShouldBe0() {
        Factor[] neighbors = new Factor[]{
            mock(Factor.class), mock(Factor.class)
        };
        Factor[] fs = buildFactors(new Maximize(), neighbors);
        Factor tested = fs[0];

        for (Factor neighbor : neighbors) {
            assertEquals(0, tested.getMessage(neighbor), Constants.DELTA);
        }
    }

    /**
     * Adds the list of factors as neighbors of the given one.
     *
     * @param f Factor to which others will be added as neighbors
     * @param neighbors List of neighboring factors to add
     */
    protected void link(Factor<Factor> f, Factor[] neighbors) {
        for (Factor n : neighbors) {
            f.addNeighbor(n);
        }
    }

    /**
     * Generates and returns a random value in the [-1, 1) range.
     *
     * @return random cost/utility
     */
    protected double getRandomValue() {
        return generator.nextDouble()*2 - 1;
    }

    /**
     * Generates and returns a random integer in the [0, n) range.
     *
     * @param n maximum integer (exclusive)
     * @return random integer
     */
    protected int getRandomIntValue(int n) {
        return generator.nextInt(n);
    }

    @Test
    public void testSetGetMessage() {
        Factor sender = mock(Factor.class);
        Factor[] factors = buildFactors(new Maximize(), new Factor[]{sender});
        double message = 10.0;
        Factor f = factors[0];
        f.addNeighbor(sender);
        f.receive(message, sender);
        assertEquals(message, f.getMessage(sender), Constants.DELTA);
    }

    /**
     * Tests the messages sent by a specific implementation of a factor against those sent by
     * a more generic factor.
     * <p/>
     * For instance, this can be used to test the {@link es.csic.iiia.bms.factors.CardinalityFactor}'s messages against
     * those sent by a {@link es.csic.iiia.bms.factors.StandardFactor} using an equivalent potential table.
     */
    @Test
    public void crossTestMessages() {
        for (int i=0; i<NUMBER_OF_RUNS; i++) {
            int len = getRandomIntValue(MAX_NEIGHBORS) + 1;
            double[] values = new double[len];
            for (int j=0; j<len; j++) {
                values[j] = generator.nextDouble() - 0.5;
            }
            LOG.log(Level.FINEST, "Messages: {0}", Arrays.toString(values));

            runAgainstGeneric(new Maximize(), values);
            runAgainstGeneric(new Minimize(), values);
        }
    }

    /**
     * Tests the evaluation of a configuration computed by a specific implementation of a factor
     * against the same computation using a more general factor.
     * <p/>
     * For instance, this can be used to test the {@link es.csic.iiia.bms.factors.CardinalityFactor}'s evaluation against
     * the evaluation performed using a {@link es.csic.iiia.bms.factors.StandardFactor} with an equivalent potential table.
     */
    @Test
    public void crossTestEvaluation() {
        for (int i=0; i<NUMBER_OF_RUNS; i++) {
            int len = getRandomIntValue(MAX_NEIGHBORS) + 1;
            boolean[] values = new boolean[len];
            for (int j=0; j<len; j++) {
                values[j] = generator.nextDouble() > 0.5;
            }
            LOG.log(Level.FINEST, "Values: {0}", Arrays.toString(values));

            testEvaluation(new Maximize(), values);
            testEvaluation(new Minimize(), values);
        }
    }

    private void testEvaluation(MaxOperator op, boolean[] values) {
        final int nNeighbors = values.length;

        // Create the neighbors and value map
        HashMap<Factor, Boolean> valuesMap = new HashMap<Factor, Boolean>();
        Factor[] neighbors = new Factor[nNeighbors];
        for (int i=0; i<nNeighbors; i++) {
            neighbors[i] = mock(Factor.class);
            neighbors[i].setIdentity(neighbors[i]);
            valuesMap.put(neighbors[i], values[i]);
        }

        // Create the factors to test (one of the specific type being tested, and a standard one)
        Factor[] factors = buildFactors(op, neighbors);
        Factor<Factor> testedSpecific = factors[0];
        Factor<Factor> testedGeneric = factors[1];

        final double expected = testedGeneric.evaluate(valuesMap);
        final double actual = testedSpecific.evaluate(valuesMap);
        assertEquals("Factor evaluation differs", expected, actual, Constants.DELTA);
    }

    /**
     * Checks the outgoing max-sum's messages of a specific factor type against those computed in
     * the standard manner.
     *
     * @param op Maximization operator to use
     * @param inMessages list of incoming messages (one per neighbor)
     */
    public void runAgainstGeneric(MaxOperator op, double[] inMessages) {
        final int nNeighbors = inMessages.length;
        final CommunicationAdapter<Factor> comSpecific = mock(CommunicationAdapter.class);
        final CommunicationAdapter<Factor> comGeneric = mock(CommunicationAdapter.class);

        // Create the neighbors
        Factor[] neighbors = new Factor[nNeighbors];
        for (int i=0; i<nNeighbors; i++) {
            neighbors[i] = mock(Factor.class);
        }

        // Create the factors to test (one of the specific type being tested, and a standard one)
        Factor[] factors = buildFactors(op, neighbors);
        Factor<Factor> testedSpecific = factors[0];
        Factor<Factor> testedGeneric = factors[1];
        testedSpecific.setCommunicationAdapter(comSpecific);
        testedSpecific.setIdentity(testedSpecific);
        testedGeneric.setCommunicationAdapter(comGeneric);
        testedGeneric.setIdentity(testedGeneric);

        // Input incoming messages
        for (int i=0; i<nNeighbors; i++) {
            testedSpecific.receive(inMessages[i], neighbors[i]);
            testedGeneric.receive(inMessages[i], neighbors[i]);
        }

        // Gather outgoing messages
        ArgumentCaptor<Double> specificMessagesCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Double> genericMessagesCaptor = ArgumentCaptor.forClass(Double.class);

        // This makes the factor run and send messages through the mocked com, capturing their
        // outputs.
        testedSpecific.run();
        testedGeneric.run();
        for (Factor neighbor : neighbors) {
            verify(comSpecific).send(specificMessagesCaptor.capture(), same(testedSpecific), same(neighbor));
            verify(comGeneric).send(genericMessagesCaptor.capture(), same(testedGeneric), same(neighbor));
        }

        // Finally verify that the messages match
        List<Double> specificMessages = specificMessagesCaptor.getAllValues();
        List<Double> genericMessages = genericMessagesCaptor.getAllValues();
        for (int i=0; i<neighbors.length; i++) {
            assertEquals("Failed with neighbor " + i, genericMessages.get(i), specificMessages.get(i), Constants.DELTA);
        }

    }

}
