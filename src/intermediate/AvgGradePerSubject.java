package intermediate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Problem 2: Average Grade Per Subject
// Input: List<Student> where each student has a subject and grade
// Output: Map<String, Double> => subject -> average grade
// Example:
// Input: [
//   Student("Math", 90), Student("Math", 80),
//   Student("English", 70), Student("English", 100)
// ]
// Output: {Math=85.0, English=85.0}
public class AvgGradePerSubject {

    @SuppressWarnings("all")
    public static class Student {
        String subject;
        double grade;

        public Student(String subject, double grade) {
            this.subject = subject;
            this.grade = grade;
        }

        public String getSubject() { return subject; }
        public double getGrade() { return grade; }
    }

    /**
     * Calculates the average grade for each subject.
     * Uses Collectors.groupingBy to group students by subject and
     * Collectors.averagingDouble to calculate the mean grade.
     * 
     * @param students List of students with their subjects and grades
     * @return Map where:
     *         - Key: subject name
     *         - Value: average grade for that subject
     */
    public static Map<String, Double> averageGradePerSubject(List<Student> students) {
        return students.stream().collect(
                // Group students by their subject
                Collectors.groupingBy(
                        Student::getSubject,
                        // Calculate average grade for each subject
                        Collectors.averagingDouble(Student::getGrade)
                )
        );
    }

    public static void main(String[] args) {
        // Problem 2
        List<Student> students = List.of(
                new Student("Math", 90),
                new Student("Math", 80),
                new Student("English", 70),
                new Student("English", 100)
        );
        System.out.println("Problem 2: " + averageGradePerSubject(students));
    }
}
