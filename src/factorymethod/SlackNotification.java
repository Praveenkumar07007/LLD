package factorymethod;

public class SlackNotification implements Notification{
  @Override
  public void send(String message) {
    System.out.println("sending slack Notification" + message);
  }
}
