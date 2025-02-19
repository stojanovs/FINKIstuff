import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.*;
//
/*
Студентски досиеа (60 поени)
Да се имплементира класа StudentRecords која ќе чита од влезен тек (стандарден влез, датотека, ...) податоци за студентски досиеа. Податоците содржат (код - единствен стринг), насока (стринг од 3 букви) и низа со оценки (цели броеви од 6 - 10). Сите податоци се разделени со едно празно место. Пример за форматот на податоците:

ioqmx7 MT 10 8 10 8 10 7 6 9 9 9 6 8 6 6 9 9 8

Ваша задача е да ги имплементирате методите:

StudentRecords() - default конструктор
int readRecords(InputStream inputStream) - метод за читање на податоците кој враќа вкупно прочитани записи
void writeTable(OutputStream outputStream) - метод кој ги печати сите записите за сите студенти групирани по насока ( најпрво се печати името на насоката), а потоа се печатат сите записи за студентите од таа насока сортирани според просекот во опаѓачки редослед (ако имаат ист просек според кодот лексикографски) во формат kod prosek, каде што просекот е децимален број заокружен на две децимали. Пример jeovz8 8.47. Насоките се сортирани лексикографски. Комплексноста на методот да не надминува O(N) во однос на бројот на записи.
void writeDistribution(OutputStream outputStream) - метод за печатење на дистрибуцијата на бројот на оценки по насока, притоа насоките се сортирани по бројот на десетки во растечки редослед (прва е насоката со најмногу оценка десет). Дистрибуцијата на оценки се печати во следниот формат:
NASOKA [оценка со 2 места порамнети во десно] | [по еден знак * на секои 10 оценки] ([вкупно оценки])

Пример:

KNI 6 | ***********(103) 7 | ******************(173) 8 | *******************(184) 9 | *****************(161) 10 | **************(138)

Комплексноста на овој метод да не надминува O(N∗M∗log2(M)) за N записи и M насоки.
*/

public class StudentRecords {
    private final Map<String, Set<Record>> records;
    private final Map<String, List<Integer>> grades;

    public StudentRecords() {
        this.records = new HashMap<>();
        this.grades = new HashMap<>();
    }

    public int readRecords(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        AtomicInteger n = new AtomicInteger();

        br.lines().forEach(line -> {
            String[] tokens = line.split("\\s+");
            List<Integer> list = IntStream.range(2, tokens.length).mapToObj(i -> Integer.parseInt(tokens[i])).collect(Collectors.toList());

            Record record = new Record(tokens[0], tokens[1], list);

            records.putIfAbsent(tokens[1], new TreeSet<>());
            records.computeIfPresent(tokens[1], (k, v) -> {
                v.add(record);
                return v;
            });

            grades.putIfAbsent(tokens[1], new ArrayList<>());
            grades.computeIfPresent(tokens[1], (k, v) -> {
                v.addAll(list);
                return v;
            });

            n.getAndIncrement();
        });

        return n.get();
    }

    public void writeTable(PrintStream out) {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));

        records.forEach((k, v) -> {
            pw.println(k);
            v.forEach(pw::println);
        });

        pw.flush();
    }

    public void writeDistribution(PrintStream out) {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
        Comparator<Map.Entry<String, List<Integer>>> comparator = (a, b) -> Long.compare(b.getValue().stream().filter(i -> i == 10).count(), a.getValue().stream().filter(i -> i == 10).count());

        grades
                .entrySet()
                .stream()
                .sorted(comparator)
                .forEach(entry -> {
                    pw.println(entry.getKey());

                    for (int i = 6; i < 11; i++) {
                        int finalI = i;
                        long n = entry.getValue().stream().filter(grade -> grade == finalI).count();

                        pw.printf("%2d | %s(%d)%n", i, asterisks(n), n);
                    }
                });

        pw.flush();
    }

    private String asterisks(long n) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < Math.ceil(n / 10.0); i++) {
            sb.append("*");
        }

        return sb.toString();
    }
}