package net.echo.math.activation.impl;

import net.echo.math.activation.Activation;

public class GELUActivation implements Activation {

    @Override
    public double activate(double input) {
        return 0.5 * input * (1 + Math.tanh(Math.sqrt(2 / Math.PI) * (input + 0.044715 * Math.pow(input, 3))));
    }

    @Override
    public double getDerivative(double input) {
        double tanhTerm = Math.tanh(Math.sqrt(2 / Math.PI) * (input + 0.044715 * Math.pow(input, 3)));
        return 0.5 * (1 + tanhTerm) + 0.5 * input * (1 - Math.pow(tanhTerm, 2)) * Math.sqrt(2 / Math.PI) * (1 + 3 * 0.044715 * Math.pow(input, 2));
    }
}
