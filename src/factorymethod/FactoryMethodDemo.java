package factorymethod;

public class FactoryMethodDemo {

  public static void main(String[] args) {

    NotificationCreator creator;

    creator = new EmailNotificationCreator();
    creator.send("Welcome to our platform!");

    creator = new SMSNotificationCreator();
    creator.send("Your OTP is 123456");

    creator = new PushNotificationCreator();
    creator.send("You have a new follower!");

    creator = new SlackNotificationCreator();
    creator.send("Standup in 10 minutes!");

    creator = new WhatsAppNotificationCreator();
    creator.send("Hello from whatsapp");
  }
}