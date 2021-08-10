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
        //summary
        data.groupingMethod(scanner);
    }
    //select between continent of location

}
class Data{
    private enum RegionType{continent,location};
    //info
    private RegionType rtype;
    private String geometricArea;
    private ArrayList<Date> timeRange=new ArrayList<Date>();
    private TimeGroupController tgc;
    //prevent running if info is empty
    private boolean infoacquired=false;
    private boolean grouped=false;
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
        //loop to find the next or previous days depend on the fact start day is after or before the end day
        if(startDayInMillisecond<endDayInMillisecond)
        {
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
            long runnInMillisecond=endDayInMillisecond;
            timeRange.add(endDate);
            while(runnInMillisecond<startDayInMillisecond)
            {
                runnInMillisecond+=dayInMillisecond;
                Date date=new Date(runnInMillisecond);
                timeRange.add(date);
            }
        }
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
        int inbetweenDistance=(fromDir)?1:-1;
        long dayInMillisecond=24*60*60*1000;
        long rootDayInMillisecond=rootDate.getTime();
        long runDayInMillisecond=rootDayInMillisecond;
        for(int i=0;i<amount;i++)
        {
            runDayInMillisecond+=inbetweenDistance*dayInMillisecond;
            Date date=new Date(runDayInMillisecond);
            timeRange.add(date);
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
        System.out.println("--------------------(^◡^ )--------------------");
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
    //separate days into time group
    public void groupingMethod(Scanner sc)
    {
        if(!infoacquired)
        {
            System.out.println(("pls add info to this data"));
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
                tgc.AddGroupAtIndex(lastadded, new TimeGroup(d));
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
                TimeGroup timeGroup=new TimeGroup(result);
                for(int j=0;j<result;j++)
                {
                    timeGroup.addDate(timeRange.get(lastaddedDate));
                    //System.out.println("last added Date: "+lastaddedDate+" "+timeRange.get(lastaddedDate));
                    lastaddedDate++;
                }
                tgc.AddGroupAtIndex(lastadded, timeGroup);
                //groups[lastadded]=timeGroup;
                lastadded++;
                //new group
            }
            for(int i=0;i<remain;i++){
                TimeGroup timeGroup=new TimeGroup(result+1);
                for(int j=0;j<result+1;j++)
                {
                    timeGroup.addDate(timeRange.get(lastaddedDate));
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
                        TimeGroup timeGroup=new TimeGroup(dateAmount);
                        for(int j=0;j<dateAmount;j++){
                        timeGroup.addDate(timeRange.get(lastaddedDate));
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
}
class TimeGroup{
    private Date[] dates;
    int lastAdded;//the index of the lasted added date
    //constructor
    TimeGroup(Date date)
    {
        dates=new Date[1];
        dates[0]=date;
    }
    TimeGroup(int amount)
    {
        dates=new Date[amount];
    }
    //add new date into array
    public void addDate(Date date)
    {
        if(lastAdded<dates.length)
        {
            dates[lastAdded]=date;
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
}