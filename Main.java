import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;  
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
public class Main{
    public static void main(String[] args)throws Exception{
        //data collection
        Scanner scanner=new Scanner(System.in);
        //ask for region name and time range
        Data data=new Data();
        data.askForDetail(scanner);
        //data.showDays();
        //summary
        data.askForMetric(scanner);
        data.showTimeMetricGroup();
    }
    //select between continent of location

}
class Data{
    private enum RegionType{continent,location};
    //info
    private RegionType rtype=RegionType.continent;
    private String geometricArea;
    private ArrayList<Date> timeRange=new ArrayList<Date>();
    private TimeGroupController tgc;
    //prevent running if info is empty
    private boolean infoacquired=false;
    private boolean grouped=false;
    //to check if a date is in a certain range
    private long smallestDateInMillisecond=0;
    private long largestDateInMillsecond=0;
    //Assign value for 2 particular dates
    private void SetData(String regionName,int regionType,Date startDate,Date endDate){
        //name
        geometricArea=regionName;
        //type
        if(regionType==1)
        {
            rtype=RegionType.continent;
        }
        else if(regionType==2)
        {
            rtype=RegionType.location;
        }
        else{
            System.out.print("Cant find region Type");
        }
        //time
        long dayInMillisecond=24*60*60*1000;
        long startDayInMillisecond=startDate.getTime();
        long endDayInMillisecond=endDate.getTime();  
        //to determinne if a date is within the given range
        smallestDateInMillisecond=startDayInMillisecond;
        largestDateInMillsecond=endDayInMillisecond;
        //loop to find the next or previous days depend on the fact start day is after or before the end day
        if(startDayInMillisecond<endDayInMillisecond)
        {
            smallestDateInMillisecond=startDayInMillisecond;
            largestDateInMillsecond=endDayInMillisecond;
            timeRange.add(startDate);
            long runnInMillisecond=startDayInMillisecond;
            while(runnInMillisecond<endDayInMillisecond)
            {
                runnInMillisecond+=dayInMillisecond;
                Date date=new Date(runnInMillisecond);
                timeRange.add(date);
            }
        }
        else{
            smallestDateInMillisecond= endDayInMillisecond;
            largestDateInMillsecond=startDayInMillisecond;
            long runnInMillisecond=endDayInMillisecond;
            timeRange.add(endDate);
            while(runnInMillisecond<startDayInMillisecond)
            {
                runnInMillisecond+=dayInMillisecond;
                Date date=new Date(runnInMillisecond);
                timeRange.add(date);
            }
        }
        //
    }
    //Assign for a range of dates
    private void SetData(String regionName,int regionType,Date rootDate,boolean fromDir, int amount){
        //name
        geometricArea=regionName;
        //type
        if(regionType==1)
        {
            rtype=RegionType.continent;
        }
        else if(regionType==2)
        {
            rtype=RegionType.location;
        }
        else{
            System.out.println("Cant find region Type:"+regionType);
        }
        //time
        //timeRange.add(rootDate);
        long dayInMillisecond=24*60*60*1000;
        long rootDayInMillisecond=rootDate.getTime();
        //always start at the smallest day
        //so array list will have a goinng up figure
        long runDayInMillisecond=(fromDir)?(rootDayInMillisecond-dayInMillisecond):(rootDayInMillisecond-(amount)*dayInMillisecond);
        for(int i=0;i<amount;i++)
        {
            runDayInMillisecond+=dayInMillisecond;
            Date date=new Date(runDayInMillisecond);
            timeRange.add(date);
            //to determin if a day is within the given range
            if(i==0)
            {
                smallestDateInMillisecond=runDayInMillisecond;
            }
            if(i==amount-1)
            {
                largestDateInMillsecond=runDayInMillisecond;
            }
        }
    }
    //acquire information
    private int askForRegionType(Scanner sc)
    {
        boolean inputAccepted=false;
        while(!inputAccepted)
        {
            System.out.println("Select region type: \n 1.Continent \n 2.Location");
            System.out.print("Enter the number:");
            int chosen=sc.nextInt();
            sc.nextLine();//scanner will start at the new line
            if(chosen==1||chosen==2)
            {
                inputAccepted=true;
                return chosen;
            }
        }

        return 0;//may not run
    }
    
    public void askForDetail(Scanner sc)throws Exception{
        //region
        System.out.print(("Enter the area name:"));
        String areaName=sc.nextLine();
        int regionType=askForRegionType(sc);
        if(regionType==0)
        {
            //fatal error
            infoacquired=false;
            return;
        }
        System.out.println("region Name:"+areaName+"regionType"+regionType);
        //time
        boolean inputAccepted=false;
        while(!inputAccepted)
        {
            System.out.println("--------------------(^◡^ )--------------------");
            System.out.println("Select Input Method: \n 1. A pair of start date and end date \n 2.A number of days or weeks FROM a particular date \n 3.A number of days or weeks TO a particular days \n");
            System.out.print("Enter the number: ");
            int chosenTime=sc.nextInt();
            sc.nextLine();//start scanner at new line
            SimpleDateFormat formatter=new SimpleDateFormat("MM/dd/yyyy");  
            if(chosenTime==1)
            {
                //input dates
                System.out.println("Enter the start date(MM/dd/yyyy):");
                String date1=sc.nextLine();
                System.out.println("Enter the end date(MM/dd/yyyy):"); 
                String date2=sc.nextLine();
                //converting and create Data
                try{
                Date startDate=formatter.parse(date1);
                Date endDate=formatter.parse(date2);
                //System.out.println("StartDate is: "+startDate+", endDate is: "+endDate);
                System.out.println("--------------------(^◡^ )--------------------");
                SetData(areaName, regionType, startDate, endDate);
                //Data data=new Data(areaName,regionType,startDate,endDate);
                infoacquired=true;
                return;
                //end loop
                }
                catch(ParseException e){
                    e.printStackTrace();    
                    System.out.println("day inputs in wrong format");
                }
                //restart
            }
            else if(chosenTime==2||chosenTime==3)
            {
                //get the rootdate
                System.out.println("Enter the start date(MM/dd/yyyy):");
                String date1=sc.nextLine();
                try{
                    Date startDate=formatter.parse(date1);
                    System.out.println("Enter a period(ex 3 days, 9 weeks)"); 
                    String period=sc.nextLine();
                    //separate by space
                    String[] parts=period.split(" ");
                    int amount=0;
                    try{
                        amount=Integer.parseInt(parts[0]);
                        //create Data object
                        //day-days-Day-Days are accepted
                        System.out.println("--------------------(^◡^ )--------------------");
                        if(parts[1].equals("days")||parts[1].equals("day")||parts.equals("Days")||parts[1].equals("Day"))
                        {
                            //System.out.println( "amount is:"+amount);
                            if(chosenTime==2)
                            {
                                SetData(areaName,regionType,startDate,true,amount);
                                infoacquired=true;
                                return;
                            }
                            if(chosenTime==3)
                            {
                                SetData(areaName,regionType,startDate,false,amount);
                                infoacquired=true;
                                return;
                            }
                        }
                        //weeks-week-Weeks-Week are accepted;
                        else if(parts[1].equals("weeks")||parts[1].equals("week")||parts[1].equals("Week")||parts[1].equals("Weeks"))
                        {
                            amount*=7;
                            //System.out.println( "amount is:"+amount);
                            if(chosenTime==2)
                            {
                                SetData(areaName,regionType,startDate,true,amount);
                                infoacquired=true;
                                return;
                            }
                            if(chosenTime==3)
                            {
                                SetData(areaName,regionType,startDate,false,amount);
                                infoacquired=true;
                                return;
                            }
                        }
                        else{
                            System.out.println(parts[1]+" is not the correct input! pls try again");

                        }
                    }
                    //error when converting string to date
                    catch(NumberFormatException e)
                    {
                        System.out.println("input may be not correct");
                    }
                }
                //error when converting string to date
                catch(ParseException e){
                    e.printStackTrace();    
                    System.out.println("day inputs in wrong format");
                }
            }
            else{
            //invalid input
            //restart the loop
                }
        }
        infoacquired=false;
        return;
    }
    //display all days in time range
    public void showDays()
    {
        //System.out.println("--------------------(^◡^ )--------------------");
        for(Date d:timeRange)
        {
            System.out.println(d);
        }
        System.out.println("--------------------(^◡^ )--------------------");
    }
    //display time groups
    public void showTimeGroup()
    {
        if(!grouped)
        {
            System.out.println("Groups has not been created! pls run grouping method");
        }
        tgc.ShowGroups();
    }
    //display time and metric in group
    public void showTimeMetricGroup()
    {
        if(!grouped)
        {
            System.out.println("Groups has not been created! pls run ask for metric first");
        }
        tgc.ShowTimeMetricGroup();
    }
    //check if a test day is within the inputtimeRange
    //retrun the index of this in the array
    private int IsBetweenGivenDays(Date date)
    {
        if(!infoacquired)
        {
            System.out.println("Pls run ask fo Detail first to continue!");
            System.out.println("--------------------( っ- ‸ – c)--------------------");
            return -1;
        }
        if(date.getTime()<=largestDateInMillsecond&&date.getTime()>=smallestDateInMillisecond){
            //test day is in the given range
            long offset=date.getTime()-smallestDateInMillisecond;
            long dayInMillisecond=24*3600*1000;
            long index=offset/dayInMillisecond;
            int i =(int)index;
            return i;
        }
        else{
            return  -1;
        }
    }
    //show metric value with days
    //used to test value 
    private void DisplayMetric(int[] values,int metric)
    {
        String metricName="";
        if(metric==1)
        {
            metricName="New Cases";
        }
        if(metric==2)
        {
            metricName="New Death";
        }
        if(metric==3)
        {
            metricName="People vacinated";
        }
        System.out.println("Dates and Metric-"+metricName);
        for(int i=0;i<timeRange.size();i++)
        {
            System.out.println(timeRange.get(i)+" - "+values[i]);
        }
        System.out.println("--------------------(^◡^ )--------------------");
    }
    //separate days into time group
    public void groupingMethod(Scanner sc,int[] values,int metricType)
    {
        if(!infoacquired)
        {
            System.out.println(("pls run askForDetail First"));
            System.out.println("--------------------(^◡^ )--------------------");
            return;
        }
        System.out.println("Choose a grouping method: \n 1. No Grouping \n 2. Number of groups \n 3. Number of days");
        System.out.printf("Enter the number :");
        int grouping=sc.nextInt();
        sc.nextLine();//scanner will start at the new line
        tgc=new TimeGroupController();
        if(grouping==1){
            //nogrouping
            tgc.SetGroupAmount(timeRange.size());
            //groups=new TimeGroup[timeRange.size()];
            int lastadded=0;//index fo groups in loop
            for(Date d: timeRange)
            {
                //System.out.println("added day"+d);
                tgc.AddGroupAtIndex(lastadded, new TimeGroup(d,values[lastadded],metricType));
                //groups[lastadded]=new TimeGroup(d);
                lastadded++;
            }
            grouped=true;
        }
        if(grouping==2){
            //by groups
            System.out.printf("Enter the amount of group:");
            int groupAmount=sc.nextInt();
            sc.nextLine();//scanner will start at the new line
            tgc.SetGroupAmount(groupAmount);
            //compute the amount of date in a group
            int total=timeRange.size();
            int result=total/groupAmount;
            int remain=total%groupAmount;
            int missing=groupAmount-remain;
            //--
            //missing is the amount of group having result dates
            //remain is the amount of group having result+1 dates
            int lastadded=0;//the last group which has been filled
            int lastaddedDate=0;//the last days which has been filled
            for(int i=0;i<missing;i++){
                TimeGroup timeGroup=new TimeGroup(result,metricType);
                for(int j=0;j<result;j++)
                {
                    timeGroup.addDate(timeRange.get(lastaddedDate),values[lastaddedDate]);
                    //System.out.println("last added Date: "+lastaddedDate+" "+timeRange.get(lastaddedDate));
                    lastaddedDate++;
                }
                tgc.AddGroupAtIndex(lastadded, timeGroup);
                //groups[lastadded]=timeGroup;
                lastadded++;
                //new group
            }
            for(int i=0;i<remain;i++){
                TimeGroup timeGroup=new TimeGroup(result+1,metricType);
                for(int j=0;j<result+1;j++)
                {
                    timeGroup.addDate(timeRange.get(lastaddedDate),values[lastaddedDate]);
                    //System.out.println("last added Date: "+lastaddedDate+" "+timeRange.get(lastaddedDate));
                    lastaddedDate++;
                }
                tgc.AddGroupAtIndex(lastadded, timeGroup);
                lastadded++;
                //new group
            }
            grouped=true;
            //tgc.ShowGroups();
        }
        if(grouping==3){
            //by days
            while(true){
                System.out.printf("Enter the amount of dates in a group:");
                int dateAmount=sc.nextInt();
                sc.nextLine();//scanner will start at the new line
                int total=timeRange.size();
                if(dateAmount==0)
                {
                    System.out.println("SYSTEM FUNCTIONAL, YOUR INPUT TERRIBLE!");
                    System.out.println("pls try again");
                    System.out.println("Enter a number that is not 0");
                    System.out.println("--------------------(^◡^ )--------------------");
                    //restart
                }
                else if(total%dateAmount!=0){
                
                    System.out.println("SYSTEM FUNCTIONAL, YOUR INPUT TERRIBLE!");
                    System.out.println("pls try again");
                    System.out.println("pls Enter a number that is divisible by "+total);
                    System.out.println("--------------------(^◡^ )--------------------");
                }  
                else{
                    int lastadded=0;
                    int lastaddedDate=0;
                    int result=total/dateAmount;
                    tgc.SetGroupAmount(result);
                    //result is the amount of group
                    //date AMount is equally the amount of date each group
                    for(int i=0;i<result;i++){
                        TimeGroup timeGroup=new TimeGroup(dateAmount,metricType);
                        for(int j=0;j<dateAmount;j++){
                        timeGroup.addDate(timeRange.get(lastaddedDate),values[lastaddedDate]);
                        //System.out.println("last added Date: "+lastaddedDate+" "+timeRange.get(lastaddedDate));
                        lastaddedDate++;
                        }
                        tgc.AddGroupAtIndex(lastadded, timeGroup);
                        //groups[lastadded]=timeGroup;
                        lastadded++;
                    }
                    grouped=true;
                    return;
                }

            }
        }
        System.out.println("--------------------(^◡^ )--------------------");
    }
    //ask for a metric
    //get info from CSV file
    //will automatically run grouping here
    public void askForMetric(Scanner sc) throws Exception
    {
        if(!infoacquired)
        {
            System.out.println("Pls run ask fo Detail first to continue!");
            System.out.println("--------------------(^◡^ )--------------------");
            return;
        }
        boolean inputAccepted=false;
        int metric=0;
        while(!inputAccepted)
        {
            System.out.println("Choose a metric: \n 1. Positive Cases \n 2. Deaths \n 3. People vacinated");
            System.out.printf("Enter the number :");
            metric=sc.nextInt();
            if(metric!=1&&metric!=2&&metric!=3)
            {
            System.out.println("Invalid Input!,try again");
            System.out.println("--------------------(^◡^ )--------------------");
            }
            else{
                inputAccepted=true;
                System.out.println("--------------------(^◡^ )--------------------");
            }
        }
        sc.nextLine();//scanner will start at the new line
        //contain the values of of all day within the given timerange
        int[] values=new int[timeRange.size()];
        Scanner input =new Scanner(new File("covid-data.csv"));
        // /sc.useDelimiter(",");
        boolean firstLine=true;//ignore the title line
        boolean nameCheck=false;//check if there is geometric match any in CSV file
        boolean dateCheck=false;
        //loading bar
        int loadingProcess=0;
        while(input.hasNextLine()){
            if(firstLine){
                input.nextLine();
                firstLine=false;
                //System.out.println("First Line Ignored");
                continue;
            }
            if(!firstLine){
                loadingProcess++;
                if(loadingProcess%2198==0)
                {
                    System.out.print("=");
                }
                String line=input.nextLine();
                //System.out.println("nextline is:"+line);
                //System.out.println("lines: "+line);
                String[] components=line.split(",");
                //compare geomatric name with the csv
                SimpleDateFormat formatter=new SimpleDateFormat("MM/dd/yyyy"); 
                if(rtype==RegionType.continent){
                    //extract info
                    //System.out.println("Continent Check");
                    if(components[1].equals(geometricArea))
                    {
                        if(!nameCheck)
                        {
                            nameCheck=true;
                        }
                        String testDayInString=components[3];
                        try{
                            Date testDate=formatter.parse(testDayInString);
                            //compare days
                            int index=IsBetweenGivenDays(testDate);
                            if(index!=-1)
                            {
                                
                                if(dateCheck==false)
                                {
                                    dateCheck=true;
                                }
                                //adding metricvalue to values array
                                //new case and new death
                                if(metric==1)
                                {
                                    String newCase=components[4];
                                    if(!newCase.equals(""))
                                    {
                                    int caseInInnt=Integer.parseInt(newCase);
                                    values[index]+=caseInInnt;
                                    }
                                }
                                else if(metric==2)
                                {
                                    String newCase=components[5];
                                    if(!newCase.equals(""))
                                    {
                                    int caseInInnt=Integer.parseInt(newCase);
                                    values[index]+=caseInInnt;
                                    }
                                }
                                //people vacinated
                                else if(metric ==3)
                                {
                                    String newCase=components[6];
                                    if(!newCase.equals(""))
                                    {
                                        int caseInInnt=Integer.parseInt(newCase);
                                        values[index]+=caseInInnt;
                                    }
                                    //else caseInInt=0 - no need to add
                                }
                            }
                            else{
                                //System.out.println("invaliddates: "+testDate);
                            }
                        }
                        catch(ParseException e)
                        {
                            e.printStackTrace();    
                            System.out.println("CSV may not be inn the correct format or incorrect column read");
                        }
                    }
                    // else{
                    //     System.out.println("not match"+components[1]+"geometric: "+geometricArea);
                    // }
                }
                else if(rtype==RegionType.location){
                    if(components[2].equals(geometricArea))
                    {
                        if(!nameCheck)
                        {
                            nameCheck=true;
                        }
                        String testDayInString=components[3];
                        try{
                            Date testDate=formatter.parse(testDayInString);
                            //compare days
                            int index=IsBetweenGivenDays(testDate);
                            if(index!=-1)
                            {
                                System.out.println("Date in check:"+ testDate);
                                if(dateCheck==false)
                                {
                                    dateCheck=true;
                                }
                                //adding metricvalue to values array
                                //new case and new death
                                if(metric==1)
                                {
                                    String newCase=components[4];
                                    if(!newCase.equals(""))
                                    {
                                    int caseInInnt=Integer.parseInt(newCase);
                                    values[index]+=caseInInnt;
                                    }
                                }
                                else if(metric==2)
                                {
                                    String newCase=components[5];
                                    if(!newCase.equals(""))
                                    {
                                    int caseInInnt=Integer.parseInt(newCase);
                                    values[index]+=caseInInnt;
                                    }
                                }
                                //people vacinated
                                else if(metric ==3)
                                {
                                    String newCase=components[6];
                                    if(!newCase.equals(""))
                                    {
                                    int caseInInnt=Integer.parseInt(newCase);
                                    values[index]+=caseInInnt;
                                    }
                                }
                            }
                        }
                        catch(ParseException e)
                        {
                            e.printStackTrace();    
                            System.out.println("CSV may not be inn the correct format or incorrect column read");
                        }
                    }
                }
                else{
                    //not likely to occur as rtype has a default of regiontype.continent
                    System.out.println("Region Type missing");
                    System.out.println("Pls run ask for detail again");
                    System.out.println("--------------------( っ- ‸ – c)--------------------");
                }
            }
        }
        System.out.println();
        //System.out.println("-------------------COMPLETE-------------------");
        if(!nameCheck)
        {
            System.out.println("your geometric input was not correct");
           
        }
        if(!dateCheck)
        {
            System.out.println("your timerange input was not included in our data!, sorry for this inconvenience!");
            System.out.println("--------------------( っ- ‸ – c)--------------------");
        }
        //show metric value
        //DisplayMetric(values, metric);
        groupingMethod(sc, values,metric);
        input.close();
    }
}
//contain all an array of time group
//a singleton to help adding or displaying timegroup faster
class TimeGroupController{

    private TimeGroup[] groups;
    //init group array
    public void SetGroupAmount(int amount){
        groups=new TimeGroup[amount];
    }
    public void AddGroupAtIndex(int index,TimeGroup g)
    {
        groups[index]=g;
    }
    //show all days in each groups
    public void ShowGroups()
    {
        System.out.println(groups.length+" groups");
        for(TimeGroup g:groups)
        {
            if(g!=null)
            {
                g.DisplayDates();
           
            }
        }
    }
    public void ShowTimeMetricGroup()
    {
        System.out.println(groups.length+" groups");
        for(TimeGroup g:groups)
        {
            if(g!=null)
            {
                g.DisplayMetric();
                g.CalculateNewTotal();
                g.displayTotalofNewCase();
            }
        }
    }
}
class TimeGroup{
    private Date[] dates;
    private int[] metricValue;
    private int totalMetricValue;
    private int metricType=0;
    private int lastAdded;//the index of the lasted added date
    //constructor
    TimeGroup(Date date,int v,int type)
    {
        dates=new Date[1];
        metricValue=new int[1];
        dates[0]=date;
        metricValue[0]=v;
        metricType=type;
    }
    TimeGroup(int amount,int type)
    {
        dates=new Date[amount];
        metricValue=new int[amount];
        metricType=type;
    }
    //add new date into array
    public void addDate(Date date,int v)
    {
        if(lastAdded<dates.length)
        {
            dates[lastAdded]=date;
            metricValue[lastAdded]=v;
            lastAdded++;
        }
        else{
            System.out.println("time group is at max- System error");
        }  
    }
    //display all days in this group
    public void DisplayDates()
    {
        System.out.println("dates in the group: "+dates.length);
        for(int i=0;i<dates.length;i++)
        {
            System.out.println(dates[i]);
        }
        System.out.println("--------------------(^◡^ )--------------------");
    }
    //display all date and metric value
    public void DisplayMetric()
    {
        System.out.println("dates and metric in the group: "+dates.length);
        for(int i=0;i<dates.length;i++)
        {
            System.out.println(dates[i]+" Metric Value: "+metricValue[i]);
        }
        System.out.println("--------------------(^◡^ )--------------------");
    }
    public void CalculateNewTotal() {
        if ( metricType == 3) {
            int new_vaccinated;
            for (int i = 0; i < metricValue.length; i++) {
                if (metricValue[i+1] == 0) {
                    new_vaccinated = metricValue[i+1] + metricValue[i];
                    totalMetricValue = new_vaccinated;
                } else {
                    new_vaccinated = metricValue[i+1] - metricValue[i];
                    totalMetricValue = new_vaccinated;
                }
            }
        }
        else {
            for (int i = 0; i < metricValue.length; i++) {
                totalMetricValue += metricValue[i];
            }
        }
    }

    public void displayTotalofNewCase(){
        System.out.println("Total is: %d"+ totalMetricValue);
        System.out.println("--------------------(^◡^ )--------------------");
    }
}
