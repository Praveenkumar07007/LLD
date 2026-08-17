package factorymethod;

public class PushNotification implements Notification{

  @Override
  public void send(String message) {
    System.out.println("sending push Notification : " + message);
  }
}
