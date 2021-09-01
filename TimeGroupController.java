//to contain all an array of time group
//a singleton to help adding or displaying time group faster
public class TimeGroupController {
    private TimeGroup[] groups;
    private int[] groupsValue; // depends on up to or new total
    private int dateBeforeValue;
    private int metricType;
    private int calculationType;

    // to initialize group array
    public void SetGroupAmount(int amount) {
        groups = new TimeGroup[amount];
    }

    public void SetMetricType(int metric) {
        metricType = metric;
    }

    public void AddGroupAtIndex(int index, TimeGroup g) {
        groups[index] = g;
    }

    // when calculating vaccinated, it requires extra value to compute total value
    // for the first group
    public void AddExtraValue(int amount) {
        dateBeforeValue += amount;
        System.out.println(("value is:" + dateBeforeValue));
    }

    // loop through groups and extract the total value within the group
    public void CalculateInGroup(int calculationType) {
        // to check calculationType
        if (calculationType != 1 && calculationType != 2) {
            System.out.println("This calculation type input is invalid. Please try again!");
        } else {
            // to save calculation type
            this.calculationType = calculationType;
        }
        // 1 - total
        // 2 - up to
        // to check if metric type has been updated;
        if (metricType == 0) {
            // metric has not been updated;
            return;
        }

        // to initialize group value
        groupsValue = new int[groups.length];

        // to store the largest of the previous
        int previous = dateBeforeValue;

        // to get value
        for (int i = 0; i < groups.length; i++) {
            int temp = groups[i].GetMaxValue(); // this is total value within the group
            if (metricType == 3) {
                if (calculationType == 1) {
                    // total is calculated by the the current largest - the previous largest
                    // the first group start with the date before value
                    int valueToSave = temp - previous;
                    previous = temp;
                    // store value to save into group value
                    groupsValue[i] = valueToSave;
                } else {
                    // up to is simple, it is the largest value into group
                    groupsValue[i] = temp;
                }
            } else {
                if (calculationType == 1) {
                    groupsValue[i] = temp;
                } else {
                    // up to is equal to the total value of the current + total value of the
                    // previous
                    previous += temp;
                    // add pre date data
                    System.out.println(("ingroup:" + temp));
                    ;
                    previous += dateBeforeValue;
                    groupsValue[i] = previous;
                }
            }
        }
    }

    // GET VALUE for display
    // calculation value
    public int GetCalculationValue() {
        return calculationType;
    }

    // get timeGroup
    public TimeGroup[] GetTimeGroup() {
        return groups;
    }

    // get computed value
    public int[] GetValue() {
        return groupsValue;
    }

    // get the date summary of this controller
    // return the first day of the first group and the last day of the last group
    public String toString() {
        return "BETWEEN " + groups[0].getDate(true) + " AND " + groups[groups.length - 1].getDate(false);
    }

    // get metric Type
    public String GetMetricType() {
        if (metricType == 1) {
            return "NEW CASES ";
        } else if (metricType == 2) {
            return "DEATHS ";
        } else if (metricType == 3) {
            return "PEOPLE VACCINATED ";
        }
        return "";
    }
}
