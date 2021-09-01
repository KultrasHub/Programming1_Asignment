import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.*;

public class Data {
    private enum RegionType {
        continent, country
    }

    // info
    private RegionType region_type = RegionType.continent;
    private String geographic_area;
    private final ArrayList<Date> time_range = new ArrayList<>();
    private TimeGroupController tgc;

    // to prevent the program to run if info is empty
    private boolean info_acquired = false;
    private boolean grouped = false;

    // to check if a date is in a certain range
    private long smallestDateInMillisecond = 0;
    private long largestDateInMillisecond = 0;

    // to get the previous value to calculate the first group metric value
    private Date dateBefore;

    // constructor: to assign value for 2 particular dates
    // all days in the range is saved in time_range
    // date before is also calculated
    private void SetData(String regionName, int regionType, Date startDate, Date endDate) {
        // time group controller
        tgc = new TimeGroupController();

        // name
        geographic_area = regionName;

        // type
        if (regionType == 1) {
            region_type = RegionType.continent;
        } else if (regionType == 2) {
            region_type = RegionType.country;
        }

        // time
        long dayInMillisecond = 24 * 60 * 60 * 1000;
        long startDayInMillisecond = startDate.getTime();
        long endDayInMillisecond = endDate.getTime();

        // loop to find the next or previous days depend on the fact start day is after
        // or before the end day
        if (startDayInMillisecond < endDayInMillisecond) {
            smallestDateInMillisecond = startDayInMillisecond;
            largestDateInMillisecond = endDayInMillisecond;
            time_range.add(startDate);
            long runInMillisecond = startDayInMillisecond;
            while (runInMillisecond < endDayInMillisecond) {
                runInMillisecond += dayInMillisecond;
                Date date = new Date(runInMillisecond);
                time_range.add(date);
            }
        } else {
            smallestDateInMillisecond = endDayInMillisecond;
            largestDateInMillisecond = startDayInMillisecond;
            long runInMillisecond = endDayInMillisecond;
            time_range.add(endDate);
            while (runInMillisecond < startDayInMillisecond) {
                runInMillisecond += dayInMillisecond;
                Date date = new Date(runInMillisecond);
                time_range.add(date);
            }
        }

        // to calculate the date before the smallest date
        dateBefore = new Date(smallestDateInMillisecond - dayInMillisecond);
    }

    // to assign value for a range of dates
    private void SetData(String regionName, int regionType, Date rootDate, boolean fromDir, int amount) {
        // time group controller
        tgc = new TimeGroupController();

        // name
        geographic_area = regionName;

        // to check region type and skip the function if region_type is missing
        if (regionType == 1) {
            region_type = RegionType.continent;
        } else if (regionType == 2) {
            region_type = RegionType.country;
        }

        // time
        // time_range.add(rootDate);
        long dayInMillisecond = 24 * 60 * 60 * 1000;
        long rootDayInMillisecond = rootDate.getTime();
        // always start from the smallest day so array list will have a going up figure
        long runDayInMillisecond = (fromDir) ? (rootDayInMillisecond - dayInMillisecond)
                : (rootDayInMillisecond - (amount * dayInMillisecond));
        for (int i = 0; i < amount; i++) {
            runDayInMillisecond += dayInMillisecond;
            Date date = new Date(runDayInMillisecond);
            time_range.add(date);
            // to determine if a day is within the given range
            if (i == 0) {
                smallestDateInMillisecond = runDayInMillisecond;
            }
            if (i == amount - 1) {
                largestDateInMillisecond = runDayInMillisecond;
            }
        }
        // to calculate date before
        dateBefore = new Date(smallestDateInMillisecond - dayInMillisecond);
    }

    // to acquire information about region and redo if input is failed
    private int askForRegionType(Scanner sc) {
        boolean inputAccepted = false;
        while (!inputAccepted) {
            System.out.println("Select a region type: \n 1. Continent \n 2. Country");
            System.out.print("Enter a number: ");
            String selectedType = sc.nextLine();

            if (selectedType.equals("1") || selectedType.equals("2")) {
                int type = Integer.parseInt(selectedType);
                if (type == 1 || type == 2) {
                    inputAccepted = true;
                    return type;
                }
            }
            System.out.println("=".repeat(100));
            System.out.println("Invalid input. Please try again!");
            System.out.println("=".repeat(100));
        }
        // may not run
        return 0;
    }

    // to acquire information about calculation method
    public void askForCalculateType(Scanner sc) {
        while (true) {
            System.out.println("Select a calculate type: \n 1. New total \n 2. Up to");
            System.out.print("Enter a number: ");
            String selectedType = sc.nextLine();
            if (selectedType.equals("1") || selectedType.equals("2")) {
                tgc.CalculateInGroup(Integer.parseInt(selectedType));
                return;
            } else {
                System.out.println("=".repeat(100));
                System.out.println("Invalid input. Please try again!");
                System.out.println("=".repeat(100));
            }
        }
    }

    /*
     * 1. to acquire information about region and dates 2. to save all dates within
     * the given range into time_range 3. //find the date before the first date
     */
    public void askForDetail(Scanner sc) {
        // region
        int regionType = askForRegionType(sc);
        // error
        if (regionType == 0) {
            info_acquired = false;
            return;
        }
        System.out.print("Enter the region name: ");
        String areaName = sc.nextLine();
        System.out.println("=".repeat(100));

        // time range
        boolean inputAccepted = false;
        while (!inputAccepted) {
            System.out.println(
                    "Select a time range: \n 1. A pair of start date and end date \n 2. A number of days or weeks FROM a particular date \n 3. A number of days or weeks TO a particular date");
            System.out.print("Enter a number: ");
            String selectedTimeRange = sc.nextLine();
            if (selectedTimeRange.equals("1") || selectedTimeRange.equals("2") || selectedTimeRange.equals("3")) {
                int selectedTime = Integer.parseInt(selectedTimeRange);
                SimpleDateFormat dateFormatted = new SimpleDateFormat("MM/dd/yyyy");
                if (selectedTime == 1) {
                    System.out.print("Enter the start date (MM/dd/yyyy): ");
                    String start_date = sc.nextLine();
                    System.out.print("Enter the end date (MM/dd/yyyy): ");
                    String end_date = sc.nextLine();
                    // to convert and create data
                    try {
                        Date startDate = dateFormatted.parse(start_date);
                        Date endDate = dateFormatted.parse(end_date);
                        System.out.println("=".repeat(100));
                        SetData(areaName, regionType, startDate, endDate);
                        // Data data=new Data(areaName,regionType,startDate,endDate);
                        info_acquired = true;
                        return;
                    } catch (ParseException e) {
                        e.printStackTrace();
                        System.out.println("Incorrect date format. Please try again!");
                    }
                } else if (selectedTime == 2 || selectedTime == 3) {
                    System.out.print("Enter a particular date (MM/dd/yyyy): ");
                    String part_date = sc.nextLine();
                    try {
                        Date particularDate = dateFormatted.parse(part_date);
                        System.out.print("Enter a period in days or weeks (eg. 3 days or 9 weeks): ");
                        String periodInput = sc.nextLine();
                        // to separate by blank space
                        String[] period = periodInput.split(" ");
                        int duration;
                        try {
                            duration = Integer.parseInt(period[0]);
                            // to create Data object
                            System.out.println("=".repeat(100));
                            if (period[1].equalsIgnoreCase("days") || period[1].equalsIgnoreCase("day")) {
                                if (selectedTime == 2) {
                                    SetData(areaName, regionType, particularDate, true, duration);
                                    info_acquired = true;
                                    return;
                                }
                                if (selectedTime == 3) {
                                    SetData(areaName, regionType, particularDate, false, duration);
                                    info_acquired = true;
                                    return;
                                }
                            }
                            // weeks-week-Weeks-Week are accepted;
                            else if (period[1].equalsIgnoreCase("weeks") || period[1].equalsIgnoreCase("week")) {
                                duration *= 7;
                                if (selectedTime == 2) {
                                    SetData(areaName, regionType, particularDate, true, duration);
                                    info_acquired = true;
                                    return;
                                }
                                if (selectedTime == 3) {
                                    SetData(areaName, regionType, particularDate, false, duration);
                                    info_acquired = true;
                                    return;
                                }
                            } else {
                                System.out.println(period[1] + " is not the correct input. Please try again");
                            }
                        }
                        // error when converting string to date
                        catch (NumberFormatException e) {
                            System.out.println("Input is incorrect!");
                        }
                    }
                    // error when converting string to date
                    catch (ParseException e) {
                        e.printStackTrace();
                        System.out.println("Incorrect date format. Please try again!");
                    }
                }
            } else {
                System.out.println("=".repeat(100));
                System.out.println("Invalid input. Please try again!");
                System.out.println("=".repeat(100));
            }
        }
        info_acquired = false;
    }

    // to check if the test date is within the input time range
    // to return the index of this in the array
    private int IsBetweenGivenDays(Date date) {
        if (!info_acquired) {
            System.out.println("Please run the Ask for Detail first to continue!");
            System.out.println("=".repeat(100));
            return -1;
        }
        if (date.getTime() <= largestDateInMillisecond && date.getTime() >= smallestDateInMillisecond) {
            // the test day is in the given range
            long offset = date.getTime() - smallestDateInMillisecond;
            long dayInMillisecond = 24 * 3600 * 1000;
            long index = offset / dayInMillisecond;
            return (int) index;
        } else {
            return -1;
        }
    }

    // to separate date into time group
    public void groupingMethod(Scanner sc, int[] values, int metricType) {
        if (!info_acquired) {
            System.out.println("Please run the Ask for Detail first to continue!");
            System.out.println("=".repeat(100));
            return;
        }
        boolean inputAccepted = false;
        while (!inputAccepted) {
            System.out.println("There are " + time_range.size() + " dates found, how do want you to group?");
            System.out
                    .println("Select a grouping method: \n 1. No Grouping \n 2. Number of groups \n 3. Number of days");
            System.out.print("Enter a number: ");
            String groupingInput = sc.nextLine();
            // to check if the input equal to 1, 2 or 3
            // loop if failed
            if (!groupingInput.equals("1") && !groupingInput.equals("2") && !groupingInput.equals("3")) {
                System.out.println("=".repeat(100));
                System.out.println("Invalid input. Please try again!");
                System.out.println("=".repeat(100));
                continue;
            }
            inputAccepted = true;
            int grouping = Integer.parseInt(groupingInput);

            // no grouping
            if (grouping == 1) {
                tgc.SetGroupAmount(time_range.size());
                // groups = new TimeGroup[time_range.size()];
                // index fo groups in loop
                int lastAdded = 0;
                for (Date d : time_range) {
                    tgc.AddGroupAtIndex(lastAdded, new TimeGroup(d, values[lastAdded], metricType, grouping));
                    // groups[lastAdded] = new TimeGroup(d);
                    lastAdded++;
                }
                grouped = true;
            }

            // number of groups
            if (grouping == 2) {
                System.out.print("Enter the amount of group: ");
                int groupAmount = sc.nextInt();
                sc.nextLine();
                tgc.SetGroupAmount(groupAmount);
                // compute the amount of date in a group
                int total = time_range.size();
                int result = total / groupAmount;
                int remain = total % groupAmount; // remain = the amount of group having result+1 dates
                int missing = groupAmount - remain; // missing = the amount of group having result dates
                int lastAdded = 0; // the last group which has been filled
                int lastAddedDate = 0;// the last days which has been filled

                for (int i = 0; i < missing; i++) {
                    TimeGroup timeGroup = new TimeGroup(result, metricType, grouping);
                    for (int j = 0; j < result; j++) {
                        timeGroup.addDate(time_range.get(lastAddedDate), values[lastAddedDate]);
                        lastAddedDate++;
                    }
                    tgc.AddGroupAtIndex(lastAdded, timeGroup);
                    // groups[lastAdded] = timeGroup;
                    lastAdded++;
                }

                for (int i = 0; i < remain; i++) {
                    TimeGroup timeGroup = new TimeGroup(result + 1, metricType, grouping);
                    for (int j = 0; j < result + 1; j++) {
                        timeGroup.addDate(time_range.get(lastAddedDate), values[lastAddedDate]);
                        lastAddedDate++;
                    }
                    tgc.AddGroupAtIndex(lastAdded, timeGroup);
                    lastAdded++;
                }
                grouped = true;
                // tgc.ShowGroups();
            }

            // number of days
            if (grouping == 3) {
                while (true) {
                    System.out.print("Enter the amount of days within a group: ");
                    int dayAmount = sc.nextInt();
                    sc.nextLine();
                    int total = time_range.size();
                    if (dayAmount == 0) {
                        System.out.println("Invalid input. Please enter a number which is greater than 0!");
                        System.out.println("=".repeat(100));
                    } else if (total % dayAmount != 0) {
                        System.out.println("Invalid input. Please enter a number that is divisible by " + total);
                        System.out.println("=".repeat(100));
                    } else {
                        int lastAdded = 0;
                        int lastAddedDate = 0;
                        int result = total / dayAmount; // result = the amount of group
                        tgc.SetGroupAmount(result);
                        // day amount = the amount of date each group
                        for (int i = 0; i < result; i++) {
                            TimeGroup timeGroup = new TimeGroup(dayAmount, metricType, grouping);
                            for (int j = 0; j < dayAmount; j++) {
                                timeGroup.addDate(time_range.get(lastAddedDate), values[lastAddedDate]);
                                lastAddedDate++;
                            }
                            tgc.AddGroupAtIndex(lastAdded, timeGroup);
                            // groups[lastAdded]=timeGroup;
                            lastAdded++;
                        }
                        grouped = true;
                        return;
                    }
                }
            }
        }
        System.out.println("=".repeat(100));
    }

    // to ask information about metric
    // to get info from CSV file
    // the program will automatically run grouping here
    public void askForMetric(Scanner sc) throws Exception {
        // check for precondition to run this function
        if (!info_acquired) {
            System.out.println("Missing Ask for Detail!");
            System.out.println("Executing Ask for Detail");
            askForDetail(sc);
            System.out.println("=".repeat(100));
            return;
        }
        // to ask for metric and loop if input is invalid
        boolean inputAccepted = false;
        int metric = 0;
        while (!inputAccepted) {
            System.out.println("Select a metric: \n 1. Positive Cases \n 2. Deaths \n 3. People vaccinated");
            System.out.print("Enter a number: ");
            String metricInput = sc.nextLine();
            if (metricInput.equals("1") || metricInput.equals("2") || metricInput.equals("3")) {
                metric = Integer.parseInt(metricInput);
                inputAccepted = true;
            } else {
                System.out.println("=".repeat(100));
                System.out.println("Invalid input. Please try again!");
                System.out.println("=".repeat(100));
            }
        }

        // to assign metric type to time group controller
        tgc.SetMetricType(metric);

        // to contain the values of days within the given time range
        int[] values = new int[time_range.size()];
        Scanner input = new Scanner(new File("covid-data.csv"));
        // /sc.useDelimiter(",");
        boolean firstLine = true; // to ignore the title line
        boolean nameCheck = false; // to check if there is geographic_area match any in CSV file
        boolean dateCheck = false;
        int loadingProcess = 0; // loading bar
        int previousValue = 0; // " " in vaccinated is converted to previousValue
        String previousLocation = ""; // to reset previous value when location change

        // to get information and save it into values
        while (input.hasNextLine()) {
            if (firstLine) {
                input.nextLine();
                firstLine = false;
                // System.out.println("First Line Ignored");
                continue;
            }
            if (!firstLine) {
                loadingProcess++;
                if (loadingProcess % 2198 == 0) {
                    System.out.print("-");
                }
                String line = input.nextLine();
                String[] components = line.split(",");
                // to compare geographic name with the one in csv
                SimpleDateFormat dateFormatted = new SimpleDateFormat("MM/dd/yyyy");
                // to check continent
                if (region_type == RegionType.continent) {
                    // to extract info
                    // System.out.println("Continent Check");
                    if (components[1].equals(geographic_area)) {
                        if (!nameCheck) {
                            nameCheck = true;
                        }
                        if (!previousLocation.equals(components[2])) {
                            previousValue = 0;
                            previousLocation = components[2];
                        }
                        String testDayInString = components[3];
                        try {
                            Date testDate = dateFormatted.parse(testDayInString);
                            // to compare days
                            int index = IsBetweenGivenDays(testDate);
                            if (index != -1) {
                                if (!dateCheck) {
                                    dateCheck = true;
                                }
                                // to add metric value to values array
                                // new case and new death
                                if (metric == 1) {
                                    String newCase = components[4];
                                    if (!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        values[index] += caseInInt;
                                    }
                                } else if (metric == 2) {
                                    String newCase = components[5];
                                    if (!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        values[index] += caseInInt;
                                    }
                                }
                                // people vaccinated
                                else if (metric == 3) {
                                    String newCase = components[6];
                                    if (!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        values[index] += caseInInt;
                                        previousValue = caseInInt;
                                    } else {
                                        values[index] += previousValue;
                                    }
                                }
                            } else {
                                // this allows value in unselected to be kept
                                if (metric == 1) {
                                    String newCase = components[4];
                                    if (!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        previousValue += caseInInt;
                                        if (dateBefore.equals(testDate)) {
                                            tgc.AddExtraValue((previousValue));
                                            // signal time group controller is
                                        }
                                        // values[index] += caseInInt;
                                    }
                                } else if (metric == 2) {
                                    String newCase = components[5];
                                    if (!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        previousValue += caseInInt;
                                        if (dateBefore.equals(testDate)) {
                                            tgc.AddExtraValue((previousValue));
                                            // signal time group controller is
                                        }
                                    }
                                }
                                if (metric == 3) {
                                    // to check if date is the date before time range
                                    // to store this value to calculate total in the time group controller
                                    String newCase = components[6];
                                    if (!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        if (dateBefore.equals(testDate)) {
                                            tgc.AddExtraValue((caseInInt));
                                            // signal time group controller is
                                        }
                                        previousValue = caseInInt;
                                    } else {
                                        if (dateBefore.equals(testDate)) {
                                            tgc.AddExtraValue((previousValue));
                                            // signal time group controller is
                                        }
                                    }
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            System.out.println("CSV file may not be in the correct format or incorrect column read");
                        }
                    }
                } else if (region_type == RegionType.country) {
                    if (components[2].equals(geographic_area)) {
                        if (!nameCheck) {
                            nameCheck = true;
                        }
                        String testDayInString = components[3];
                        try {
                            Date testDate = dateFormatted.parse(testDayInString);
                            // to compare days
                            int index = IsBetweenGivenDays(testDate);
                            if (index != -1) {
                                if (!dateCheck) {
                                    dateCheck = true;
                                }
                                // to add metric value to values array
                                // new case and new death
                                if (metric == 1) {
                                    String newCase = components[4];
                                    if (!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        values[index] += caseInInt;
                                    }
                                } else if (metric == 2) {
                                    String newCase = components[5];
                                    if (!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        values[index] += caseInInt;
                                    }
                                }
                                // people vaccinated
                                else if (metric == 3) {
                                    String newCase = components[6];
                                    if (!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        values[index] += caseInInt;
                                        previousValue = caseInInt;
                                    } else {
                                        values[index] += previousValue;
                                    }
                                }
                            } else {
                                if (metric == 1) {
                                    String newCase = components[4];
                                    if (!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        previousValue += caseInInt;
                                        if (dateBefore.equals(testDate)) {
                                            tgc.AddExtraValue((caseInInt));
                                            // signal time group controller is
                                        }
                                        // values[index] += caseInInt;
                                    }
                                } else if (metric == 2) {
                                    String newCase = components[5];
                                    if (!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        previousValue += caseInInt;
                                        if (dateBefore.equals(testDate)) {
                                            tgc.AddExtraValue((caseInInt));
                                            // signal time group controller is
                                        }
                                    }
                                }
                                if (metric == 3) {
                                    String newCase = components[6];
                                    if (!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        if (dateBefore.equals(testDate)) {
                                            // signal time group controller is
                                            tgc.AddExtraValue(caseInInt);
                                        }
                                        previousValue = caseInInt;
                                    } else {
                                        if (dateBefore.equals(testDate)) {
                                            // signal time group controller is
                                            tgc.AddExtraValue(previousValue);
                                        }
                                    }
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            System.out.println("CSV file may not be inn the correct format or incorrect column read");
                        }
                    }
                } else {
                    // not likely to occur as region_type has a default of RegionType.continent
                    System.out.println("Missing region type. Please run the Ask for Detail again!");
                    System.out.println("=".repeat(100));
                }
            }
        }
        System.out.println();
        // this part is to prevent error
        if (!nameCheck) {
            System.out.println("Your geographic area input was not correct");
            System.out.println("=".repeat(100));
        }
        if (!dateCheck) {
            System.out.println("Your time range input was not included in our data! Sorry for this inconvenience!");
            System.out.println("=".repeat(100));
        }
        // to show metric value
        // DisplayMetric(values, metric);
        groupingMethod(sc, values, metric);
        input.close();
    }

    // ask for display
    public void askForDisplay(Scanner sc) {
        if (!grouped) {
            System.out.println("Run grouping first");
            System.out.println("=".repeat(100));
            return;
        }
        System.out.println("=".repeat(100));
        // ask for display type
        boolean inputAccepted = false;
        int displayValue = 0;
        while (!inputAccepted) {
            System.out.println("Select display method: \n 1. Tabular Display \n 2. Chart Display");
            System.out.print("Enter a number: ");
            String metricInput = sc.nextLine();
            if (metricInput.equals("1") || metricInput.equals("2")) {
                displayValue = Integer.parseInt(metricInput);
                inputAccepted = true;
            } else {
                System.out.println("=".repeat(100));
                System.out.println("Invalid input. Please try again!");
                System.out.println("=".repeat(100));
            }
        }
        // value should be 1 or 2 at this point
        // input accepted
        displayTable_Chart(displayValue - 1);
    }

    // to display days, metric and calculation value in the group
    // input will determine which to display
    // 0-table only,
    // 1- chart only
    // 2-both
    private void displayTable_Chart(int input) {
        if (input == 0 || input == 2) {
            Display.displayTable(tgc.GetTimeGroup(), tgc.GetValue(), tgc.GetCalculationValue(), geographic_area,
                    tgc.GetMetricType(), tgc.toString());
        }
        if (input == 1 || input == 2) {
            Display.displayChart(tgc.GetTimeGroup(), tgc.GetValue(), tgc.GetMetricType(), tgc.toString(),
                    geographic_area);
        }
    }
}
