package views;

import javax.swing.*;

public class EvaluatorFrame extends JFrame {
    public EvaluatorFrame () {
        super ("Evaluator");
        setSize(400,600);
        setLocationRelativeTo(null);

        JTabbedPane evaluatorPane = new JTabbedPane();
        evaluatorPane.setBounds(450,200,600,400);
        this.add(evaluatorPane);

        // Evaluation page (includes comments and marks)
        JPanel panel1 = new JPanel();
        evaluatorPane.add("Presentation", panel1);

        
    }
}