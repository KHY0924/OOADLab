# UML Documentation

This folder contains PlantUML diagrams documenting the OOADLab Seminar Management System architecture.

## 📁 Folder Structure

```
docs/uml/
├── class-diagrams/
│   └── class-diagram.puml          # Complete class diagram
└── sequence-diagrams/
    ├── 01-student-submission.puml       # Student submission workflow
    ├── 02-evaluator-evaluation.puml     # Evaluator evaluation flow
    ├── 03-coordinator-session-management.puml  # Session & assignment management
    ├── 04-report-generation.puml        # Report generation & export
    ├── 05-user-authentication.puml      # Login & role-based access
    └── 06-award-ceremony.puml           # Award calculation & ceremony
```

## 📊 Class Diagram

The class diagram shows relationships among:

| Class | Description |
|-------|-------------|
| **User** | Base class for all users (Student, Evaluator, Coordinator) |
| **Evaluator** | Extends User, evaluates submissions |
| **Coordinator** | Extends User, manages seminars and sessions |
| **Seminar** | Contains multiple sessions |
| **Session** | Presentation sessions with students and evaluators |
| **Submission** | Student presentation submissions |
| **Evaluation** | Evaluator's assessment of a submission |
| **Award** | Awards given to top performers |
| **Report** | Generated reports (StudentEvaluation, SeminarSummary) |

## 🔄 Sequence Diagrams

| # | Diagram | Description |
|---|---------|-------------|
| 1 | Student Submission | Upload, edit, and deadline checking |
| 2 | Evaluator Evaluation | View assignments and submit evaluations |
| 3 | Session Management | Create sessions, assign students/evaluators |
| 4 | Report Generation | Generate schedule, summary, and awards |
| 5 | User Authentication | Login and role-based panel routing |
| 6 | Award Ceremony | Calculate winners and generate ceremony agenda |

## 🛠️ How to View Diagrams

### Option 1: VS Code Extension
Install the "PlantUML" extension in VS Code, then open any `.puml` file and use `Alt+D` to preview.

### Option 2: Online Viewer
1. Go to [PlantUML Web Server](http://www.plantuml.com/plantuml/uml/)
2. Paste the content of any `.puml` file
3. View the rendered diagram

### Option 3: Command Line
```bash
# Install PlantUML
# Then run:
java -jar plantuml.jar docs/uml/class-diagrams/class-diagram.puml
```

## 📝 Key Relationships

```
User ──┬── Evaluator ──── evaluates ──── Submission
       │
       └── Coordinator ── manages ───── Session
                                           │
Seminar ────── contains ─────────── Session ─┼── assigned to ── Student
                                             │
                                             └── assigned to ── Evaluator

Submission ──── evaluated by ──── Evaluation

Report ──┬── StudentEvaluationReport
         └── SeminarSummaryReport ──── includes ──── Award
```
