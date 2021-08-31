import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // data collection
        Scanner scanner = new Scanner(System.in);
        // ask for region name and time range
        Data data = new Data();
        data.askForDetail(scanner);
        // data.showDays();
        // summary
        data.askForMetric(scanner);
        // data.showTimeMetricGroup();
        data.askForCalculateType(scanner);
        // display
        data.askForDisplay(scanner);
    }
}
