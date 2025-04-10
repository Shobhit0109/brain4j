package net.echo.brain4j.training.evaluation;

import net.echo.math4j.BrainUtils;
import net.echo.math4j.math.tensor.Tensor;

import java.util.Map;

public class EvaluationResult {

    private final double loss;
    private final int classes;
    private final Map<Integer, Tensor> classifications;

    private double accuracy;
    private double precision;
    private double recall;
    private double f1Score;

    public EvaluationResult(double loss, int classes, Map<Integer, Tensor> classifications) {
        this.loss = loss;
        this.classes = classes;
        this.classifications = classifications;
        calculateStats();
    }

    private void calculateStats() {
        int totalCorrect = 0;
        int totalIncorrect = 0;

        int[] truePositives = new int[classes];
        int[] falsePositives = new int[classes];
        int[] falseNegatives = new int[classes];

        for (int i = 0; i < classifications.size(); i++) {
            Tensor vector = classifications.get(i);

            for (int j = 0; j < vector.elements(); j++) {
                int value = (int) vector.get(j);

                if (i == j) {
                    totalCorrect += value;
                    truePositives[i] += value;
                } else {
                    totalIncorrect += value;
                    falsePositives[j] += value;
                    falseNegatives[i] += value;
                }
            }
        }

        double precisionSum = 0, recallSum = 0;
        for (int i = 0; i < classes; i++) {
            double precision = (truePositives[i] + falsePositives[i]) > 0 ?
                    (double) truePositives[i] / (truePositives[i] + falsePositives[i]) : 0;

            double recall = (truePositives[i] + falseNegatives[i]) > 0 ?
                    (double) truePositives[i] / (truePositives[i] + falseNegatives[i]) : 0;

            precisionSum += precision;
            recallSum += recall;
        }

        this.accuracy = (double) totalCorrect / (totalCorrect + totalIncorrect);
        this.precision = precisionSum / classes;
        this.recall = recallSum / classes;
        this.f1Score = (precision + recall) > 0 ? 2 * (precision * recall) / (precision + recall) : 0;
    }

    public String confusionMatrix() {
        StringBuilder matrix = new StringBuilder();
        String divider = BrainUtils.getHeader(" Evaluation Results ", "━");

        matrix.append(divider);
        matrix.append("Out of ").append(classifications.size()).append(" classes\n\n");

        String secondary = "%-15s %-10s\n";
        matrix.append(secondary.formatted("Loss:", "%.4f".formatted(loss)));
        matrix.append(secondary.formatted("Accuracy:", "%.4f".formatted(accuracy)));
        matrix.append(secondary.formatted("Precision:", "%.4f".formatted(precision)));
        matrix.append(secondary.formatted("Recall:", "%.4f".formatted(recall)));
        matrix.append(secondary.formatted("F1-score:", "%.4f".formatted(f1Score)));

        divider = BrainUtils.getHeader(" Confusion Matrix ", "━");
        matrix.append(divider);
        matrix.append("First column is the actual class, top row are the predicted classes.\n\n");
        matrix.append(" ");

        for (int i = 0; i < classes; i++) {
            matrix.append("%4d".formatted(i)).append(" ");
        }

        matrix.append("\n  ");
        matrix.append("-".repeat(5 + classes * 5)).append("\n");

        for (int i = 0; i < classes; i++) {
            StringBuilder text = new StringBuilder();
            Tensor predictions = classifications.get(i);

            for (int j = 0; j < predictions.elements(); j++) {
                int prediction = (int) predictions.get(j);
                text.append("%4d".formatted(prediction)).append(" ");
            }

            matrix.append("%4d | ".formatted(i));
            matrix.append(text).append("\n");
        }

        matrix.append("\n");
        matrix.append(BrainUtils.getHeader("", "━"));

        return matrix.toString();
    }

    public double loss() {
        return loss;
    }

    public int classes() {
        return classes;
    }

    public double accuracy() {
        return accuracy;
    }

    public double precision() {
        return precision;
    }

    public double recall() {
        return recall;
    }

    public double f1Score() {
        return f1Score;
    }

    public Map<Integer, Tensor> classifications() {
        return classifications;
    }
}
