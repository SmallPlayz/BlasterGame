public class Main {
    public static void main(String[] args) throws InterruptedException {
        new BlasterGame();
        BlasterGame.Threads++;
        //while(true){System.out.println(BlasterGame.Threads); Thread.sleep(100);}
    }
}