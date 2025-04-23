package net.echo.math.tensor.autograd.operations;

import net.echo.math.activation.Activation;
import net.echo.math.tensor.Tensor;
import net.echo.math.tensor.autograd.Operation;

public class ActivationOperation implements Operation {

    private final Activation activation;

    public ActivationOperation(Activation activation) {
        this.activation = activation;
    }

    @Override
    public Tensor forward(Tensor... inputs) {
        return activation.activate(inputs[0]);
    }

    @Override
    public Tensor[] backward(Tensor gradOutput, Tensor... inputs) {
        Tensor input = inputs[0];
        Tensor derivative = activation.getDerivative(input); // ∂activation/∂x
        Tensor gradInput = gradOutput.mul(derivative); // Chain rule: dL/dx = dL/dy * dy/dx

        return new Tensor[] { gradInput };
    }
}
