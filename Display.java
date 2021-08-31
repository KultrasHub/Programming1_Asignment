import java.util.*;

public class Display {

    // display table name
    // DISPLAY
    public static void displayTable(TimeGroup[] groups, int[] groupsValue, int calculationType, String regionName,
            String metric, String period) {
        System.out.println("=".repeat(100));
        System.out.printf("%20s", "NUMBER OF " + metric + period + " IN " + regionName);
        System.out.println();
        System.out.println();
        System.out.println("_".repeat(55));
        System.out.printf("%10s %38s", "Range", "Value");
        System.out.println();
        System.out.println("_".repeat(55));

        // row
        for (int row = 0; row < groups.length; row++) {
            if (groups[row].getGroupType() == 1) {
                // first column
                groups[row].DisplayDates();
                // second column
                // to show total or metric
                if (calculationType == 1) {
                    System.out.printf("%40s", "New total: " + groupsValue[row]);
                    System.out.println();
                }
                if (calculationType == 2) {
                    System.out.printf("%40s", "Up to: " + groupsValue[row]);
                    System.out.println();
                }
            }
            if (groups[row].getGroupType() == 2 || groups[row].getGroupType() == 3) {
                // first column
                groups[row].DisplayDates();
                // to show total or metric
                // second column
                if (calculationType == 1) {
                    System.out.printf("%20s", "New total: " + groupsValue[row]);
                    System.out.println();
                }
                if (calculationType == 2) {
                    System.out.printf("%20s", "Up to: " + groupsValue[row]);
                    System.out.println();
                }
            }
        }
        System.out.println("_".repeat(55) + "\n");
        Conclusion.conclude();
    }

    public static void displayChart(TimeGroup[] groups, int[] groupsValue, String metric, String period,
            String regionName) {
        System.out.println("=".repeat(100));
        System.out.printf("%20s", "NUMBER OF " + metric + period + " IN " + regionName);
        System.out.println();
        System.out.println();
        String label = "Summary Data ";
        int labelLength = label.length() + 1;
        int rows = 24;
        int columns = 80;
        ArrayList<Integer> timeGroups = new ArrayList<>();
        ArrayList<Integer> summaryData = new ArrayList<>();
        String[][] chart = new String[rows][columns + labelLength];

        // initialize an empty chart
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns + labelLength; j++) {
                chart[i][j] = " ";
            }
        }

        // to find the distance between each column
        int columnDistance = (columns - 1) / (groups.length + 1);
        int columnValue;
        for (int i = 1; i <= groups.length; i++) {
            columnValue = columnDistance * i;
            timeGroups.add(columnValue);
        }

        // to find the max value
        int max = groupsValue[0];
        for (int value : groupsValue) {
            if (value > max) {
                max = value;
            }
        }

        // to find the min value
        int min = groupsValue[0];
        for (int value : groupsValue) {
            if (value < min) {
                min = value;
            }
        }

        // distance between each row
        int rowDistance = (max - min) / 22;
        for (int v : groupsValue) {
            double maxNewTotal = max;
            for (int k = 0; k < rows; k++) {
                maxNewTotal -= rowDistance;
                if (maxNewTotal < v) {
                    summaryData.add(k);
                    break;
                }
            }
        }

        // each summary data point is represented
        // add data to chart
        for (int i = 0; i < rows; i++) {
            for (int j = labelLength - 1; j < columns + labelLength; j++) {
                if (i != rows - 1) {// avoid the last row
                    for (int k = 0; k < timeGroups.size(); k++) {
                        chart[summaryData.get(k)][timeGroups.get(k) + labelLength] = "*" + groupsValue[k];
                    }
                } else {
                    chart[rows - 1][labelLength - 1] = "|";// corner
                    break;
                }
                chart[i][labelLength - 1] = "|";// last column
                if (i == 12) {
                    chart[i][labelLength - 1] = " ";
                }
                chart[rows - 1][j] = "_";// last row
            }
        }
        // add [-] under each column that contain value

        // to add title to y-axis
        chart[rows / 2][0] = label + "|";

        // to display chart
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns + labelLength; j++) {
                System.out.print(chart[i][j]);
            }
            System.out.println();
        }
        System.out.printf("%60s", "Groups");
        System.out.println();
        Conclusion.conclude();
    }

}