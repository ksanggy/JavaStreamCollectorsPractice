import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Main {

    // Student Class
    @SuppressWarnings("all")
    public static class Student {
        private final String name;
        private final String subject;
        private final double grade;

        Student(String name, String subject, double grade) {
            this.name = name;
            this.subject = subject;
            this.grade = grade;
        }

        public String getName() {
            return name;
        }

        public String getSubject() {
            return subject;
        }

        public double getGrade() {
            return grade;
        }
    }

    // want to return list of students' subject grade medians as k : v pair, where k = subject & v = median value
    // due to stream pipeline using type specific methods like Student::getSubject, the return type of method cannot be more general (as in using Generics)
    @SuppressWarnings("all")
    public static Collector<Student, ?, Map<String, Double>> getSubjectMedians() {
        return Collectors.groupingBy(
                Student::getSubject,
                Collectors.collectingAndThen(
                    Collectors.toList(), // change from mapping(Student::getGrade, toList) to make pipeline data more apparent
                    subjectStudents -> {
                        // After Java 16+, toList() already returns as unmodifiable list (immutable collections are thread safe)
                        // wrapping with unmodifiableList is redundant in this case but is explicit and is backward compatible if Java < 16
                        List<Double> grades = Collections.unmodifiableList(
                            subjectStudents.stream()
                                .map(Student::getGrade)// added with the change above since data is of list of Student not grades.
                                .sorted()
                                .toList()
                        );
                        int size = subjectStudents.size();
                        if(size % 2 == 0) {
                            return (grades.get(grades.size() / 2 - 1) + grades.get(grades.size() / 2)) / 2;
                        }
                        else {
                            return grades.get(grades.size() / 2);
                        }
                    }
        ));
    }

    public static void main( String[] args ) {

        List<Student> students = new ArrayList<>();
        students.add(new Student("Test " + 1, "Physics", 97.0));
        students.add(new Student("Test " + 2, "Physics", 77.0));
        students.add(new Student("Test " + 3, "Physics", 87.0));
        students.add(new Student("Test " + 4, "Physics", 47.0));
        students.add(new Student("Test " + 5, "Physics", 83.0));
        students.add(new Student("Test " + 6, "Biology", 63.0));
        students.add(new Student("Test " + 7, "Biology", 73.0));
        students.add(new Student("Test " + 8, "Biology", 93.0));
        students.add(new Student("Test " + 9, "Chemistry", 91.0));
        students.add(new Student("Test " + 10, "Chemistry", 81.0));

        Map<String, Double> subjectMedians = students.stream().collect(getSubjectMedians());
        System.out.println("subjectMedians = " + subjectMedians);
    }
}