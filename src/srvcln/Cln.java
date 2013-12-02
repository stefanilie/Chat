/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package srvcln;


import java.net.*;
import java.util.*;
import java.io.*;

class Cln {  // Clasa Client, cel ce se va conecta la Server
    // Clasa client are doua functionalitati, sa trimita si sa primeasca mesaje
    // Logic nu se pot face ambele simultan pe acelasi fir de executie, astfel
    // in programul de mai jos vom pastra firul principal pentru a trimite mesaje catre server
    // si vom crea un Thread nou pentru a primi mesaje de la server
    
  public static String st = "";
  public static void main(String[] args) throws Exception
  {
    String strName = "";
    Scanner sc = new Scanner(System.in); 
    System.out.print("Adresa serverului si portul : ");
    Socket cs = new Socket("localhost", 14);
    //Socket cs = new Socket( sc.next(), sc.nextInt() ); // instantiem un socket la adresa si portul citit de la tastatura
    // socket-ul va fi obiectul ce se va conecta la server si prin intermediul caruia vom transmite si primi date
    DataOutputStream os = new DataOutputStream(cs.getOutputStream()); // declaram un obiect de tipul DataOutputStream
    // ce va lucra cu fluxul de iesire al socket-ului
    final DataInputStream is = new DataInputStream(cs.getInputStream()); // analog pentru fluxul de intrare
    Thread T= new Thread(new Runnable(){  // declaram si instantiam Thread-ul ce se va ocupa cu primirea
        // mesajelor de la server
             public void run() { // metoda ce va fi apelata cand thread-ul este pornit
              while (true) {// blocam firul printr-un loop infinit ce primeste mesaje de la server
           //       String isRead = ""; // 
                  try {
                      Cln.st = is.readUTF();   // primim mesajul de la server
                      System.out.println(st);// afisam ce am primit
                  } catch (IOException ex) {
                  }
                 
              }
          }
      });
      T.start(); // pornim threadul 
      
      System.out.println("Conexiune reusita!\n Acum alege un nume:");
      strName = sc.next();
      while(strName.contains(" "))
      {
          System.out.println("Nume invalid. Nu trebuie sa contina \' \'");
          strName = sc.next();
      }
      String ceva ="/nick " + strName; 
      os.writeUTF(ceva);
      Thread.sleep(5000);
      String strResponse =  Cln.st;
      while(strResponse.contains("Nickname deja exista. Alege altul!"))
      {
          strName = sc.next();
          ceva = "/nick "+ strName;
          os.writeUTF(ceva);
          System.out.println("Ce se trimite la server: " + ceva);
          strResponse = Cln.st;
          System.out.println("ce s-a primit de la server: " + strResponse);
      }
      System.out.print("Setup finalizat! Poti conversa!");
      while (true)
      { // blocam firul principal cu un loop infinit care citeste de la tastatura mesaje
          // si le trimite prin DataOutputStream in fluxul de iesire al socket-ului catre server
          ceva = sc.next();
          os.writeUTF("/mesaj44"+ strName+ "44" +ceva);
          
      }
  }
}