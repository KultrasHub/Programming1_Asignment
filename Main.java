import java.sql.Date;
import java.util.ArrayList;
import java.util.Scanner;
public class Main{
    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);

        //System.out.printf("Enter time range")
        //Data newData=new Data("",);
        //newData.groupingMethod(scanner);
    }

}
class Data{
    //info
    private String geometricArea;
     private ArrayList<Date> timeRange=new ArrayList<Date>();
    // //constructor
    Data(String region,ArrayList<Date> dates){
        geometricArea=region;
        timeRange=dates;
    }
    public void groupingMethod(Scanner sc)
    {
        System.out.println("Grouping method: \n 1. No Grouping \n 2. Number of groups \n 3. Number of days");
        System.out.printf("Enter the number :");
        int grouping=sc.nextInt();
        TimeGroup[] groups;
        if(grouping==1)
        {
            //nogrouping
            groups=new TimeGroup[timeRange.size()];
            int lastadded=0;//index fo groups in loop
            for(Date d: timeRange)
            {
                groups[lastadded].addInstantDate(d);
                lastadded++;
            }
            //display
            for(TimeGroup g :groups){
                g.DisplayDates();
            }
        }
        if(grouping==2)
        {
            //by groups
        }
        if(grouping==3)
        {
            //by days
        }
    }
}
//
class TimeGroup{
    private Date[] dates;
    int lastAdded;//the index of the lasted added date
    //inint the dates array  
    public void setAmount(int amount){
        dates=new Date[amount];
    }
    //add new day into array
    public void addDate(Date date)
    {
        if(lastAdded<dates.length)
        {
            dates[lastAdded]=date;
        }
        else{
            System.out.println("time group is at max- System error");
        }  
    }
    //when there only one days to add in
    public void addInstantDate(Date date)
    {
        dates=new Date[1];
        dates[lastAdded]=date;
    }
    //show days
    public void DisplayDates()
    {
        System.out.print("dates in the group");
        for(int i=0;i<dates.length;i++)
        {
            System.out.print(dates[i]);
        }
        System.out.println();
    }
}