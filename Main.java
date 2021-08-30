package com.company;

import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.*;

public class Main{
    public static void main(String[] args)throws Exception {
        //data collection
        Scanner scanner = new Scanner(System.in);
        //ask for region name and time range
        Data data = new Data();
        data.askForDetail(scanner);
        //data.showDays();
        //summary
        data.askForMetric(scanner);
        //data.showTimeMetricGroup();
        data.askForCalculateType(scanner);
        data.showAllInGroup();
        // data.displayUpToValue();
    }
}
class Data {
    private enum RegionType{continent,country}

    //info
    private RegionType region_type = RegionType.continent;
    private String geographic_area;
    private final ArrayList<Date> time_range = new ArrayList<>();
    private TimeGroupController tgc;

    //to prevent the program to run if info is empty
    private boolean info_acquired = false;
    private boolean grouped = false;

    //to check if a date is in a certain range
    private long smallestDateInMillisecond = 0;
    private long largestDateInMillisecond = 0;

    //to get the previous value to calculate the first group metric value
    private Date dateBefore;

    //constructor: to assign value for 2 particular dates
    //all days in the range is saved in time_range
    //date before is also calculated
    private void SetData(String regionName, int regionType, Date startDate, Date endDate) {
        //time group controller
        tgc = new TimeGroupController();

        //name
        geographic_area = regionName;

        //type
        if (regionType == 1) {
            region_type = RegionType.continent;
        }
        else if (regionType == 2) {
            region_type = RegionType.country;
        }
        else {
            System.out.print("No region type found!");
        }

        //time
        long dayInMillisecond = 24 * 60 * 60 * 1000;
        long startDayInMillisecond = startDate.getTime();
        long endDayInMillisecond = endDate.getTime();

        //loop to find the next or previous days depend on the fact start day is after or before the end day
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
        }
        else {
            smallestDateInMillisecond = endDayInMillisecond;
            largestDateInMillisecond = startDayInMillisecond;
            long runInMillisecond = endDayInMillisecond;
            time_range.add(endDate);
            while(runInMillisecond < startDayInMillisecond) {
                runInMillisecond += dayInMillisecond;
                Date date = new Date(runInMillisecond);
                time_range.add(date);
            }
        }

        //to calculate the date before the smallest date
        dateBefore = new Date(smallestDateInMillisecond - dayInMillisecond);
    }

    //to assign value for a range of dates
    private void SetData(String regionName, int regionType, Date rootDate, boolean fromDir, int amount) {
        //time group controller
        tgc = new TimeGroupController();

        //name
        geographic_area = regionName;

        //to check region type and skip the function if region_type is missing
        if (regionType == 1) {
            region_type = RegionType.continent;
        }
        else if (regionType == 2) {
            region_type = RegionType.country;
        }
        else {
            System.out.println("No region type found!");
        }

        //time
        //time_range.add(rootDate);
        long dayInMillisecond = 24 * 60 * 60 * 1000;
        long rootDayInMillisecond = rootDate.getTime();
        //always start from the smallest day so array list will have a going up figure
        long runDayInMillisecond = (fromDir) ? (rootDayInMillisecond - dayInMillisecond) : (rootDayInMillisecond - (amount * dayInMillisecond));
        for (int i = 0; i < amount; i++) {
            runDayInMillisecond += dayInMillisecond;
            Date date = new Date(runDayInMillisecond);
            time_range.add(date);
            //to determine if a day is within the given range
            if (i == 0) {
                smallestDateInMillisecond = runDayInMillisecond;
            }
            if (i == amount - 1) {
                largestDateInMillisecond = runDayInMillisecond;
            }
        }

        //to calculate date before
        dateBefore = new Date(smallestDateInMillisecond - dayInMillisecond);
    }

    //to acquire information about region and redo if input is failed
    private int askForRegionType(Scanner sc) {
        boolean inputAccepted = false;
        while(!inputAccepted) {
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
        //may not run
        return 0;
    }

    //to acquire information about calculation method
    public void askForCalculateType(Scanner sc){
        while (true) {
            System.out.println("Select a calculate type: \n 1. New total \n 2. Up to");
            System.out.print("Enter a number: ");
            String selectedType = sc.nextLine();
            if (selectedType.equals("1") || selectedType.equals("2")) {
                tgc.CalculateInGroup(Integer.parseInt(selectedType));
                return;
            }
            else {
                System.out.println("=".repeat(100));
                System.out.println("Invalid input. Please try again!");
                System.out.println("=".repeat(100));
            }
        }
    }

    /*
    1. to acquire information about region and dates
    2. to save all dates within the given range into time_range
    3. //find the date before the first date
    */
    public void askForDetail(Scanner sc) {
        //region
        int regionType = askForRegionType(sc);
        //error
        if (regionType == 0) {
            info_acquired = false;
            return;
        }
        System.out.print("Enter the region name: ");
        String areaName = sc.nextLine();
        System.out.println("=".repeat(100));

        //time range
        boolean inputAccepted = false;
        while (!inputAccepted) {
            System.out.println("Select a time range: \n 1. A pair of start date and end date \n 2. A number of days or weeks FROM a particular date \n 3. A number of days or weeks TO a particular date");
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
                    //to convert and create data
                    try {
                        Date startDate = dateFormatted.parse(start_date);
                        Date endDate = dateFormatted.parse(end_date);
                        System.out.println("=".repeat(100));
                        SetData(areaName, regionType, startDate, endDate);
                        //Data data=new Data(areaName,regionType,startDate,endDate);
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
                        //to separate by blank space
                        String[] period = periodInput.split(" ");
                        int duration;
                        try {
                            duration = Integer.parseInt(period[0]);
                            //to create Data object
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
                            //weeks-week-Weeks-Week are accepted;
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
                        //error when converting string to date
                        catch (NumberFormatException e) {
                            System.out.println("Input is incorrect!");
                        }
                    }
                    //error when converting string to date
                    catch (ParseException e) {
                        e.printStackTrace();
                        System.out.println("Incorrect date format. Please try again!");
                    }
                }
            }
            else {
                System.out.println("=".repeat(100));
                System.out.println("Invalid input. Please try again!");
                System.out.println("=".repeat(100));
            }
        }
        info_acquired = false;
    }

    //to check if the test date is within the input time range
    //to return the index of this in the array
    private int IsBetweenGivenDays(Date date) {
        if (!info_acquired) {
            System.out.println("Please run the Ask for Detail first to continue!");
            System.out.println("=".repeat(100));
            return -1;
        }
        if (date.getTime() <= largestDateInMillisecond && date.getTime() >= smallestDateInMillisecond) {
            //the test day is in the given range
            long offset = date.getTime() - smallestDateInMillisecond;
            long dayInMillisecond = 24 * 3600 * 1000;
            long index = offset / dayInMillisecond;
            return (int)index;
        }
        else {
            return  -1;
        }
    }

    //to separate date into time group
    public void groupingMethod(Scanner sc, int[] values, int metricType) {
        if (!info_acquired) {
            System.out.println("Please run the Ask for Detail first to continue!");
            System.out.println("=".repeat(100));
            return;
        }
        boolean inputAccepted = false;
        while (!inputAccepted) {
            System.out.println("There are " + time_range.size() + " dates found, how do want you to group?");
            System.out.println("Select a grouping method: \n 1. No Grouping \n 2. Number of groups \n 3. Number of days");
            System.out.print("Enter a number: ");
            String groupingInput = sc.nextLine();
            //to check if the input equal to 1, 2 or 3
            //loop if failed
            if (!groupingInput.equals("1") && !groupingInput.equals("2") && !groupingInput.equals("3")) {
                System.out.println("=".repeat(100));
                System.out.println("Invalid input. Please try again!");
                System.out.println("=".repeat(100));
                continue;
            }
            inputAccepted = true;
            int grouping = Integer.parseInt(groupingInput);

            //no grouping
            if (grouping == 1) {
                tgc.SetGroupAmount(time_range.size());
                //groups = new TimeGroup[time_range.size()];
                //index fo groups in loop
                int lastAdded = 0;
                for (Date d : time_range) {
                    tgc.AddGroupAtIndex(lastAdded, new TimeGroup(d, values[lastAdded], metricType, grouping));
                    //groups[lastAdded] = new TimeGroup(d);
                    lastAdded++;
                }
                grouped = true;
            }

            //number of groups
            if (grouping == 2) {
                System.out.print("Enter the amount of group: ");
                int groupAmount = sc.nextInt();
                sc.nextLine();
                tgc.SetGroupAmount(groupAmount);
                //compute the amount of date in a group
                int total = time_range.size();
                int result = total / groupAmount;
                int remain = total % groupAmount; //remain = the amount of group having result+1 dates
                int missing = groupAmount - remain; //missing = the amount of group having result dates
                int lastAdded = 0; //the last group which has been filled
                int lastAddedDate = 0;//the last days which has been filled

                for (int i = 0; i < missing; i++) {
                    TimeGroup timeGroup = new TimeGroup(result, metricType, grouping);
                    for (int j = 0; j < result; j++) {
                        timeGroup.addDate(time_range.get(lastAddedDate), values[lastAddedDate]);
//                        System.out.println("last added Date: "+ lastAddedDate + " " + time_range.get(lastAddedDate));
                        lastAddedDate++;
                    }
                    tgc.AddGroupAtIndex(lastAdded, timeGroup);
                    //groups[lastAdded] = timeGroup;
                    lastAdded++;
                }

                for (int i = 0; i < remain; i++) {
                    TimeGroup timeGroup = new TimeGroup(result + 1, metricType, grouping);
                    for (int j = 0; j < result + 1; j++) {
                        timeGroup.addDate(time_range.get(lastAddedDate), values[lastAddedDate]);
//                        System.out.println("last added Date: " + lastAddedDate + " " + time_range.get(lastAddedDate));
                        lastAddedDate++;
                    }
                    tgc.AddGroupAtIndex(lastAdded, timeGroup);
                    lastAdded++;
                }
                grouped = true;
                //tgc.ShowGroups();
            }

            //number of days
            if (grouping == 3) {
                while (true) {
                    System.out.print("Enter the amount of days within a group: ");
                    int dayAmount = sc.nextInt();
                    sc.nextLine();
                    int total = time_range.size();
                    if (dayAmount == 0) {
                        System.out.println("Invalid input. Please enter a number which is greater than 0!");
                        System.out.println("=".repeat(100));
                    }
                    else if (total % dayAmount != 0) {
                        System.out.println("Invalid input. Please enter a number that is divisible by " + total);
                        System.out.println("=".repeat(100));
                    }
                    else{
                        int lastAdded = 0;
                        int lastAddedDate = 0;
                        int result = total / dayAmount; //result = the amount of group
                        tgc.SetGroupAmount(result);
                        //day amount = the amount of date each group
                        for (int i = 0; i < result; i++) {
                            TimeGroup timeGroup = new TimeGroup(dayAmount, metricType, grouping);
                            for (int j = 0; j < dayAmount; j++) {
                                timeGroup.addDate(time_range.get(lastAddedDate), values[lastAddedDate]);
                                //System.out.println("last added Date: " + lastAddedDate + " " + time_range.get(lastAddedDate));
                                lastAddedDate++;
                            }
                            tgc.AddGroupAtIndex(lastAdded, timeGroup);
                            //groups[lastAdded]=timeGroup;
                            lastAdded++;
                        }
                        grouped=true;
                        return;
                    }
                }
            }
        }
        System.out.println("=".repeat(100));
    }

    //to ask information about metric
    //to get info from CSV file
    //the program will automatically run grouping here
    public void askForMetric(Scanner sc) throws Exception {
        if (!info_acquired) {
            System.out.println("Please run the Ask for Detail first to continue!");
            System.out.println("=".repeat(100));
            return;
        }
        boolean inputAccepted = false;
        int metric = 0;
        while (!inputAccepted) {
            System.out.println("Select a metric: \n 1. Positive Cases \n 2. Deaths \n 3. People vaccinated");
            System.out.print("Enter a number: ");
            String metricInput = sc.nextLine();
            if (metricInput.equals("1") || metricInput.equals("2") || metricInput.equals("3")) {
                metric = Integer.parseInt(metricInput);
                inputAccepted = true;
            }
            else {
                System.out.println("=".repeat(100));
                System.out.println("Invalid input. Please try again!");
                System.out.println("=".repeat(100));
            }
        }

        //to assign metric type to time group controller
        tgc.SetMetricType(metric);

        //to contain the values of days within the given time range
        int[] values = new int[time_range.size()];
        Scanner input = new Scanner(new File("covid-data.csv"));
        // /sc.useDelimiter(",");
        boolean firstLine = true; //to ignore the title line
        boolean nameCheck = false; //to check if there is geographic_area match any in CSV file
        boolean dateCheck = false;
        int loadingProcess = 0; //loading bar
        int previousValue = 0; //" " in vaccinated is converted to previousValue
        String previousLocation=""; //to reset previous value when location change

        //to get information and save it into values
        while (input.hasNextLine()) {
            if (firstLine){
                input.nextLine();
                firstLine=false;
                //System.out.println("First Line Ignored");
                continue;
            }
            if(!firstLine) {
                loadingProcess++;
                if (loadingProcess % 2198 == 0) {
                    System.out.print("-");
                }
                String line = input.nextLine();
                String[] components = line.split(",");
                //to compare geographic name with the one in csv
                SimpleDateFormat dateFormatted = new SimpleDateFormat("MM/dd/yyyy");
                //to check continent
                if (region_type == RegionType.continent) {
                    //to extract info
                    //System.out.println("Continent Check");
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
                            //to compare days
                            int index = IsBetweenGivenDays(testDate);
                            if (index != -1) {
                                if (!dateCheck) {
                                    dateCheck = true;
                                }
                                //to add metric value to values array
                                //new case and new death
                                if (metric == 1) {
                                    String newCase = components[4];
                                    if (!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        values[index] += caseInInt;
                                    }
                                }
                                else if (metric == 2) {
                                    String newCase = components[5];
                                    if (!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        values[index] += caseInInt;
                                    }
                                }
                                //people vaccinated
                                else if (metric == 3) {
                                    String newCase = components[6];
                                    if(!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        values[index] += caseInInt;
                                        previousValue = caseInInt;
                                    }
                                    else {
                                        values[index] += previousValue;
                                    }
                                }
                            }
                            else {
                                //this allows value to be kept
                                if (metric == 3) {
                                    //to check if date is the date before time range
                                    //to store this value to calculate total in the time group controller
                                    String newCase = components[6];
                                    if (!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        if (dateBefore.equals(testDate)) {
                                            tgc.AddExtraValue((caseInInt));
                                            //signal time group controller is
                                        }
                                        previousValue = caseInInt;
                                    }
                                    else {
                                        if (dateBefore.equals(testDate)) {
                                            tgc.AddExtraValue((previousValue));
                                            //signal time group controller is
                                        }
                                    }
                                }
                            }
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                            System.out.println("CSV file may not be in the correct format or incorrect column read");
                        }
                    }
                }
                else if (region_type == RegionType.country) {
                    if (components[2].equals(geographic_area)) {
                        if (!nameCheck) {
                            nameCheck = true;
                        }
                        String testDayInString = components[3];
                        try {
                            Date testDate = dateFormatted.parse(testDayInString);
                            //to compare days
                            int index = IsBetweenGivenDays(testDate);
                            if (index != -1) {
                                if(!dateCheck) {
                                    dateCheck=true;
                                }
                                //to add metric value to values array
                                //new case and new death
                                if (metric == 1) {
                                    String newCase = components[4];
                                    if (!newCase.equals("")) {
                                        int caseInInt=Integer.parseInt(newCase);
                                        values[index] += caseInInt;
                                    }
                                }
                                else if (metric==2) {
                                    String newCase = components[5];
                                    if (!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        values[index] += caseInInt;
                                    }
                                }
                                //people vaccinated
                                else if (metric == 3) {
                                    String newCase = components[6];
                                    if (!newCase.equals("")) {
                                        int caseInInt=Integer.parseInt(newCase);
                                        values[index] += caseInInt;
                                        previousValue = caseInInt;
                                    }
                                    else {
                                        values[index] += previousValue;
                                    }
                                }
                            }
                            else {
                                if (metric == 3) {
                                    String newCase = components[6];
                                    if (!newCase.equals("")) {
                                        int caseInInt = Integer.parseInt(newCase);
                                        if (dateBefore.equals(testDate)) {
                                            //signal time group controller is
                                            tgc.AddExtraValue(caseInInt);
                                        }
                                        previousValue = caseInInt;
                                    }
                                    else {
                                        if(dateBefore.equals(testDate)) {
                                            //signal time group controller is
                                            tgc.AddExtraValue(previousValue);
                                        }
                                    }
                                }
                            }
                        }
                        catch(ParseException e) {
                            e.printStackTrace();
                            System.out.println("CSV file may not be inn the correct format or incorrect column read");
                        }
                    }
                }
                else {
                    //not likely to occur as region_type has a default of RegionType.continent
                    System.out.println("Missing region type. Please run the Ask for Detail again!");
                    System.out.println("=".repeat(100));
                }
            }
        }
        System.out.println();
        //this part is to prevent error
        if (!nameCheck) {
            System.out.println("Your geographic area input was not correct");
            System.out.println("=".repeat(100));
        }
        if (!dateCheck) {
            System.out.println("Your time range input was not included in our data! Sorry for this inconvenience!");
            System.out.println("=".repeat(100));
        }
        //to show metric value
        //DisplayMetric(values, metric);
        groupingMethod(sc, values, metric);
        input.close();
    }

    //DISPLAY SECTION-----------------------------------
    //this function is used for checking while development
    //show metric value with days
    //used to test value
    private void DisplayMetric(int[] values, int metric) {
        String metricName = "";
        if (metric == 1) {
            metricName="New Cases";
        }
        if (metric == 2) {
            metricName = "New Death";
        }
        if (metric == 3) {
            metricName = "People vaccinated";
        }
        System.out.println("Dates and Metric- " + metricName);
        for (int i = 0;i < time_range.size(); i++) {
            System.out.println(time_range.get(i) + " - " + values[i]);
        }
        System.out.println("=".repeat(100));
    }

    //to display all days in time range
    public void showDays() {
        for(Date d : time_range) {
            System.out.println(d);
        }
        System.out.println("=".repeat(100));
    }

    //to display time groups
    public void showTimeGroup() {
        if (!grouped) {
            System.out.println("Groups have not been created. Please run grouping method!");
        }
        tgc.ShowGroups();
    }

    //to display time and metric in group
    public void showTimeMetricGroup() {
        if(!grouped)
        {
            System.out.println("Groups have not been created. Please run grouping method!");
        }
        tgc.ShowTimeMetricGroup();
    }

    //to display days, metric and calculation value in the group
    public void showAllInGroup() {
        tgc.displayTable();
        tgc.displayChart();
    }
}

class TimeGroup {
    private final Date[] dates;
    private final int[] metricValue;
    private final int metricType;
    private final int groupType;
    private int lastAdded; //the index of the lasted added date

    //constructor
    TimeGroup(Date date, int value, int type, int grouping) {
        dates = new Date[1];
        metricValue = new int[1];
        dates[0] = date;
        metricValue[0] = value;
        metricType = type;
         groupType = grouping;
    }

    TimeGroup(int amount, int type, int grouping) {
        dates = new Date[amount];
        metricValue = new int[amount];
        metricType = type;
        groupType = grouping;
    }

    public int getGroupType() {
        return groupType;
    }

    //to add new date into array
    public void addDate(Date date,int value) {
        if (lastAdded < dates.length) {
            dates[lastAdded] = date;
            metricValue[lastAdded] = value;
            lastAdded++;
        }
        else {
            System.out.println("Time group is at maximum - System error!");
        }
    }

    //To display all dates in each group
    public void DisplayDates() {
        //to display dates if no grouping
        if (groupType == 1) {
            for (Date value : dates) {
                SimpleDateFormat dateFormatted = new SimpleDateFormat("EE MM/dd/yyyy");
                String date = dateFormatted.format(value);
                System.out.print("\n" + date);
            }
        }
        if (groupType == 2 || groupType == 3) {
            SimpleDateFormat dateFormatted = new SimpleDateFormat("EE MM/dd/yyyy");
            String date1 = dateFormatted.format(dates[0]);
            String date2 = dateFormatted.format(dates[dates.length-1]);
            System.out.print("\n" + date1 + " - " + date2);
        }
    }

    //to display all days and the respective metric values in each group
    public void DisplayMetric() {
        if (metricType == 1) {
            System.out.println("Metric - New Cases");
        }
        else if (metricType == 2) {
            System.out.println("Metric - New Death");
        }
        else if (metricType == 3) {
            System.out.println("Metric - People vaccinated");
        }
        for (int i = 0; i < dates.length; i++) {
            System.out.println(dates[i] + " Metric Value: " + metricValue[i]);
        }
    }

    public int GetMaxValue() {
        int largest = 0;
        if (metricType == 3) {
            //a2-a1+a3-a2+...+a(n-1)-a(n)=a(n-1)-a1;
            //so we find the largest number in the array and the smallest
            //[0] and [length-1] may not be correct since their value can be = 0
            for (int value : metricValue) {
                if (value != 0) {
                    if (largest < value) {
                        largest = value;
                    }
                }
            }
            //System.out.println("smallest:"+smallest+"largest:"+largest);
            //add the last value
        }
        else {
            for (int j : metricValue) {
                largest += j;
            }
        }
        return largest;
    }
}

//to contain all an array of time group
//a singleton to help adding or displaying time group faster
class TimeGroupController{
    private TimeGroup[] groups;
    private int[] groupsValue; //depends on up to or new total
    private int dateBeforeValue;
    private int metricType;
    private int calculationType;

    //to initialize group array
    public void SetGroupAmount(int amount) {
            groups = new TimeGroup[amount];
    }


    public void SetMetricType(int metric) {
            metricType = metric;
    }

    public void AddGroupAtIndex(int index,TimeGroup g) {
            groups[index] = g;
    }

    //to show metric with days in each groups
    public void ShowTimeMetricGroup() {
        System.out.println("There are " + groups.length + " groups");
        for (TimeGroup g : groups) {
            if (g != null) {
                g.DisplayMetric();
            }
        }

    }

    //when calculating vaccinated, it requires extra value to compute total value for the first group
    public void AddExtraValue(int amount) {
        dateBeforeValue += amount;
    }

    //loop through groups and extract the total value within the group
    public void CalculateInGroup(int calculationType) {
        //to check calculationType
        if (calculationType != 1 && calculationType != 2) {
            System.out.println("This calculation type input is invalid. Please try again!");
        }
        else {
            //to save calculation type
            this.calculationType = calculationType;
        }

        //1 - total
        //2 - up to
        //to check if metric type has been updated;
        if (metricType == 0) {
            //metric has not been updated;
            return;
        }

        //to initialize group value
        groupsValue = new int[groups.length];

        //to store the largest of the previous
        int previous = dateBeforeValue;

        //to get value
        for (int i = 0; i < groups.length; i++) {
            int temp = groups[i].GetMaxValue(); //this is total value within the group
            if (metricType == 3) {
                if (calculationType == 1) {
                    //total is calculated by the the current largest - the previous largest
                    //the first group start with the date before value
                    int valueToSave = temp - previous;
                    previous = temp;
                    //store value to save into group value
                    groupsValue[i] = valueToSave;
                }
                else {
                    //up to is simple, it is the largest value into group
                    groupsValue[i] = temp;
                }
            }
            else {
                if (calculationType == 1) {
                    groupsValue[i] = temp;
                }
                else{
                    //up to is equal to the total value of the current + total value of the previous
                    previous += temp;
                    groupsValue[i] = previous;
                }
            }
        }
    }

    //To show all days in each group
    public void ShowGroups() {
        for (TimeGroup g : groups) {
            if (g != null) {
                g.DisplayDates();
            }
        }
    }

    //DISPLAY
    public void displayTable() {
        System.out.println();
        System.out.println("_".repeat(55));
        System.out.printf("%10s %38s", "Range", "Value");
        System.out.println();
        System.out.println("_".repeat(55));

        for (int row = 0; row < groups.length; row++) {
            if (groups[row].getGroupType() == 1) {
                groups[row].DisplayDates();
                //to show total or metric
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
                groups[row].DisplayDates();
                //to show total or metric
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
    }

    public void displayChart() {
        String label = "Summary Data ";
        int labelLength = label.length() + 1;
        int rows = 24;
        int columns = 80;
        ArrayList<Integer> timeGroups = new ArrayList<>();
        ArrayList<Integer> summaryData = new ArrayList<>();
        String[][] chart = new String[rows][columns + labelLength];

        //initialize an empty chart
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns + labelLength; j++) {
                chart[i][j] = " ";
            }
        }

        //to find the distance between each column
        int columnDistance = (columns - 1) / (groups.length + 1);
        int columnValue;
        for (int i = 1; i <= groups.length; i++) {
            columnValue = columnDistance * i;
            System.out.println(columnValue);
            timeGroups.add(columnValue);
        }

        //to find the max value
        int max = groupsValue[0];
        for (int value : groupsValue) {
            if (value > max) {
                max = value;
            }
        }

        //to find the min value
        int min = groupsValue[0];
        for (int value : groupsValue) {
            if (value < min) {
                min = value;
            }
        }

        //distance between each row
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

        //each summary data point is represented
        for (int i = 0; i < rows; i++) {
            for (int j = labelLength - 1; j < columns + labelLength; j++) {
                if (i != rows - 1) {
                    for (int k = 0; k < timeGroups.size(); k++) {
                        chart[summaryData.get(k)][timeGroups.get(k) + labelLength] = "*" + groupsValue[k];
                    }
                }
                else {
                    chart[rows - 1][labelLength - 1] = "|";
                    break;
                }
                chart[i][labelLength - 1] = "|";
                if (i == 12) {
                    chart[i][labelLength - 1] = " ";
                }
                chart[rows - 1][j] = "_";
            }
        }

        //to add title to y-axis
        chart[rows / 2][0] = label + "|";

        //to display chart
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns + labelLength; j++) {
                System.out.print(chart[i][j]);
            }
            System.out.println();
        }
        System.out.printf("%60s", "Groups");
    }
}
