package net.echo.brain4j.loss.impl;

import net.echo.brain4j.loss.LossFunction;
import net.echo.math4j.math.tensor.Tensor;

public class MeanSquaredError implements LossFunction {

    @Override
    public double calculate(Tensor actual, Tensor predicted) {
        double loss = 0.0;

        for (int i = 0; i < actual.elements(); i++) {
            loss += Math.pow(actual.get(i) - predicted.get(i), 2);
        }

        return loss / actual.elements();
    }

    @Override
    public float getDelta(float error, float derivative) {
        return error * derivative;
    }
}
