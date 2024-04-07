package org.example;
//m
import java.io.*;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

public class RSA {

    private BigInteger p;
    private BigInteger q;
    private BigInteger n;
    private BigInteger phi;
    private BigInteger e;
    private BigInteger d;

    public RSA(BigInteger p, BigInteger q) {
        this.p = p;
        this.q = q;
        this.n = p.multiply(q);
        this.phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        this.e = calculateE();
        this.d = calculateD();
    }

    private BigInteger calculateE() {
        BigInteger e;
        do {
            e = new BigInteger(phi.bitLength(), new Random());
        } while (e.compareTo(BigInteger.ONE) <= 0 || e.compareTo(phi) >= 0 || !e.gcd(phi).equals(BigInteger.ONE));
        return e;
    }

    private BigInteger calculateD() {
        return e.modInverse(phi);
    }

    public BigInteger encrypt(BigInteger plaintext) {
        return plaintext.modPow(e, n);
    }

    public BigInteger decrypt(BigInteger ciphertext) {
        return ciphertext.modPow(d, n);
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getE() {
        return e;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the prime number p: ");
        BigInteger p = scanner.nextBigInteger();

        System.out.print("Enter the prime number q: ");
        BigInteger q = scanner.nextBigInteger();

        RSA rsa = new RSA(p, q);

        scanner.nextLine();

        int choice;
        do {
            System.out.println("\nMenu:");
            System.out.println("1. Encrypt text");
            System.out.println("2. Decrypt text");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter the text to encrypt: ");
                    String textToEncrypt = scanner.nextLine();


                    StringBuilder encryptedText = new StringBuilder();
                    for (char c : textToEncrypt.toCharArray()) {
                        BigInteger plaintext = BigInteger.valueOf(c);
                        BigInteger ciphertext = rsa.encrypt(plaintext);
                        encryptedText.append(ciphertext).append(" ");
                    }
                    System.out.println("Encrypted text: " + encryptedText.toString());

                    // Save encrypted text to file
                    try (PrintWriter writer = new PrintWriter("encrypted.txt")) {
                        writer.println(encryptedText.toString().trim());
                        System.out.println("Encrypted text saved to encrypted.txt");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 2:

                    StringBuilder encryptedInput = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new FileReader("encrypted.txt"))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            encryptedInput.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    StringBuilder decryptedText = new StringBuilder();
                    String[] encryptedParts = encryptedInput.toString().split(" ");
                    for (String part : encryptedParts) {
                        BigInteger ciphertext = new BigInteger(part);
                        BigInteger plaintext = rsa.decrypt(ciphertext);
                        decryptedText.append((char) plaintext.intValue());
                    }
                    System.out.println("Decrypted text: " + decryptedText.toString());
                    break;

                case 3:
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 3.");
            }
        } while (choice != 3);

        scanner.close();
    }
}
