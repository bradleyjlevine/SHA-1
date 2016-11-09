import java.util.Scanner;

public class Runner {

	public static void main(String[] args) {
		Scanner reader = new Scanner(System.in);
		
		System.out.println("Enter the message you would like to hash: ");
		String message = reader.nextLine();
		
		SHA1 sha = new SHA1();
		
		String hashDigest = sha.digest(message.getBytes());
		
		System.out.println("SHA-1: " + hashDigest);
		
		reader.close();
	}
}
