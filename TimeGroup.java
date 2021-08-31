import java.util.*;
import java.text.SimpleDateFormat;

public class TimeGroup {
    private final Date[] dates;
    private final int[] metricValue;
    private final int metricType;
    private final int groupType;
    private int lastAdded; // the index of the lasted added date

    // constructor
    TimeGroup(Date date, int value, int type, int grouping) {
        dates = new Date[1];
        metricValue = new int[1];
        dates[0] = date;
        metricValue[0] = value;
        metricType = type;
        groupType = grouping;
    }

    // instant construct
    TimeGroup(int amount, int type, int grouping) {
        dates = new Date[amount];
        metricValue = new int[amount];
        metricType = type;
        groupType = grouping;
    }

    public int getGroupType() {
        return groupType;
    }

    // to add new date into array
    // add value to equipvalent dates
    public void addDate(Date date, int value) {
        if (lastAdded < dates.length) {
            dates[lastAdded] = date;
            metricValue[lastAdded] = value;
            lastAdded++;
        } else {
            System.out.println("Time group is at maximum - System error!");
        }
    }

    // To display dates in each group
    // single date in no grouping
    // start and end date in other method
    public void DisplayDates() {
        // to display dates if no grouping
        if (groupType == 1) {
            for (Date value : dates) {
                System.out.print("\n" + DateToString(value));
            }
        }
        if (groupType == 2 || groupType == 3) {

            System.out.print("\n" + DateToString(dates[0]) + " - " + DateToString(dates[dates.length - 1]));
        }
    }

    // get dates in string of this group
    // used to display table name
    // get Min determine this function to return the smallest day or the largest day
    public String getDate(boolean getMin) {
        if (groupType == 1) {
            return DateToString(dates[0]);
        }
        if (groupType == 2 || groupType == 3) {
            if (getMin) {
                // get the smallest day
                return DateToString(dates[0]);
            } else {
                // get the largest day
                return DateToString(dates[dates.length - 1]);
            }
        }
        return "";
    }

    // convert days to string but remove the time field
    private String DateToString(Date input) {
        SimpleDateFormat outputFormatter = new SimpleDateFormat("MM/dd/yyyy");
        return outputFormatter.format(input);
    }

    // to display all days and the respective metric values in each group
    public void DisplayMetric() {
        if (metricType == 1) {
            System.out.println("Metric - New Cases");
        } else if (metricType == 2) {
            System.out.println("Metric - New Death");
        } else if (metricType == 3) {
            System.out.println("Metric - People vaccinated");
        }
        for (int i = 0; i < dates.length; i++) {
            System.out.println(dates[i] + " Metric Value: " + metricValue[i]);
        }
    }

    // to vaccinate return the value of the largest in a group
    // to other metric return the sum value
    public int GetMaxValue() {
        int largest = 0;
        if (metricType == 3) {
            // a2-a1+a3-a2+...+a(n-1)-a(n)=a(n-1)-a1;
            // so we find the largest number in the array and the smallest
            // [0] and [length-1] may not be correct since their value can be = 0
            for (int value : metricValue) {
                if (value != 0) {
                    if (largest < value) {
                        largest = value;
                    }
                }
            }
            // System.out.println("smallest:"+smallest+"largest:"+largest);
            // add the last value
        } else {
            for (int j : metricValue) {
                largest += j;
            }
        }
        return largest;
    }
}
