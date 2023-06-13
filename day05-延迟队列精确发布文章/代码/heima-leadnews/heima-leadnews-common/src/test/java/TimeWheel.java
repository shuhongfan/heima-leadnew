import java.util.Date;

public class TimeWheel {

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 24; j++) {//小时----时间轮
                for (int k = 0; k < 60; k++) {//分钟----时间轮
                    Thread.sleep(1000);
                    System.out.println(k+"----------"+ new Date());
                }
            }
        }
    }
}
