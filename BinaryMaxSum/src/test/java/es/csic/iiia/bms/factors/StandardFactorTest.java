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

import java.util.Arrays;

import static org.mockito.AdditionalMatchers.eq;
import static org.mockito.Mockito.*;

/**
 *
 * @author Marc Pujol <mpujol@iiia.csic.es>
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class StandardFactorTest {

    @Test
    public void testRun1() {
        final double[] values  = new double[]{0, 1, 2};

        runSelector(new Minimize(), values, new double[]{-1, 0, 0});
        runAtMostOne(new Minimize(), values, new double[]{0, 0, 0});
        runSelector(new Maximize(), values, new double[]{-2, -2, -1});
        runAtMostOne(new Maximize(), values, new double[]{-2, -2, -1});
    }

    @Test
    public void testRun2() {
        final double[] values  = new double[]{0, 0, 2};

        runSelector(new Minimize(), values, new double[]{0, 0, 0});
        runAtMostOne(new Minimize(), values, new double[]{0, 0, 0});
        runSelector(new Maximize(), values, new double[]{-2, -2, 0});
        runAtMostOne(new Maximize(), values, new double[]{-2, -2, 0});
    }

    @Test
    public void testRun3() {
        final double[] values  = new double[]{-1, 2};

        runSelector(new Minimize(), values, new double[]{-2, 1});
        runAtMostOne(new Minimize(), values, new double[]{0, 1});
        runSelector(new Maximize(), values, new double[]{-2, 1});
        runAtMostOne(new Maximize(), values, new double[]{-2, 0});
    }

    @Test
    public void testRun4() {
        MaxOperator op = new Maximize();
        final double[] values = new double[] { 9, 6 };
        final double[] results = new double[] { 6, 0 };
        final double[] potential = new double[] { 0, op.getWorstValue(), 0, 0 };

        run(op, potential, values, results);
    }

    private void runSelector(MaxOperator op, double[] values, double[] results) {
        run(op, getSelectorPotential(op, values.length), values, results);
    }

    private void runAtMostOne(MaxOperator op, double[] values, double[] results) {
        run(op, getAtMostOnePotential(op, values.length), values, results);
    }

    private void run(MaxOperator op, double[] potential, double[] values, double[] results) {
        CommunicationAdapter com = mock(CommunicationAdapter.class);

        // Setup incoming messages
        Factor[] neighbors = new Factor[values.length];
        StandardFactor tested = new StandardFactor();
        tested.setCommunicationAdapter(com);
        tested.setMaxOperator(op);
        tested.setIdentity(tested);

        for (int i=0; i<neighbors.length; i++) {
            neighbors[i] = mock(Factor.class);
            tested.addNeighbor(neighbors[i]);
            tested.receive(values[i], neighbors[i]);
        }

        tested.setPotential(potential);

        // This makes the factor run and send messages through the mocked com
        tested.run();

        for (int i=0; i<neighbors.length; i++) {
            verify(com).send(eq(results[i], Constants.DELTA), same(tested), same(neighbors[i]));
        }
    }

    private double[] getSelectorPotential(MaxOperator op, int nNeighbors) {
        // Set the factor's potential as a selector
        double[] potential = new double[1 << nNeighbors];
        Arrays.fill(potential, op.getWorstValue());
        int idx = 1;
        for (int nNeighbor=0; nNeighbor<nNeighbors; nNeighbor++) {
            potential[idx] = 0;
            idx = idx << 1;
        }
        return potential;
    }

    private double[] getAtMostOnePotential(MaxOperator op, int nNeighbors) {
        double[] potential = getSelectorPotential(op, nNeighbors);
        potential[0] = 0;
        return potential;
    }
}