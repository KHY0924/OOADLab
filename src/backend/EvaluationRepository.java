import java.io.*;
import java.util.*;

public class EvaluationRepository {
    private List<Evaluation> evaluations = new ArrayList<>();
    private final String FILE_NAME = "evaluations.txt";

    // Constructor: Load data immediately when the app starts
    public EvaluationRepository() {
        loadDataFromFile();
    }

    // UPDATED: Save to memory AND to the file
    public void saveEvaluation(Evaluation evaluation) {
        evaluations.add(evaluation);
        saveDataToFile(); //This is the new vital step
    }

    public List<Evaluation> findBySubmission(String submissionId) {
        List<Evaluation> result = new ArrayList<>();
        for (Evaluation e : evaluations) {
            if (e.getSubmissionId().equals(submissionId)) {
                result.add(e);
            }
        }
        return result;
    }

    // --- NEW HELPER METHOD: Write to file ---
    private void saveDataToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Evaluation e : evaluations) {
                // Format: submissionId,evaluatorId,score,comments
                String line = e.getSubmissionId() + "," +
                              e.getEvaluatorId() + "," +
                              e.getTotalScore() + "," +
                              e.getComments();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- NEW HELPER METHOD: Read from file ---
    private void loadDataFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return; // No file yet? Nothing to load.

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    // Reconstruct the object from text
                    String subId = parts[0];
                    int evalId = Integer.parseInt(parts[1]);
                    // Note: You might need to adjust this depending on how you store specific rubric scores vs total score
                    Evaluation e = new Evaluation(subId, evalId); 
                    e.setComments(parts[3]);
                    // You might need to add a method to 'force' the total score if you aren't saving individual rubric points
                    evaluations.add(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}