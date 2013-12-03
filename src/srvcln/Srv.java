/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package srvcln;

// importurile necesare
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;
import java.io.*;
import java.net.*;
import java.util.*;

class Srv {  // Clasa Server, locul la care se vor conecta clientii
    
  static int i=0;   // numarul de conexiuni de la deschiderea serverului
  
  public static void main(String[] arg) throws IOException {
      
    /*   In ArrayList-ul "sockete" vom stoca toate DataOutputStreamurile ce se conecteaza
     * la serverul nostru.
     *   DataOutputStream-ul este clasa ce se ocupa de fluxul de iesire al clientului
     * care s-a conecatat la server. In DataOutputStream-ul fiecarui socket ( client )
     * serverul va scrie mesajul ce doreste sa il transmita.
     */     
    ArrayList<DataOutputStream> sockete = new ArrayList<DataOutputStream>();
    ArrayList<String> listNames = new ArrayList<String>();
    
    // Obiectele de tip ServerSocket si Socket sunt cele care instantiaza conexiunea dintre Server si Client 
    ServerSocket ss = null; Socket cs = null;
    Scanner sc = new Scanner(System.in); 
    System.out.print("Portul : ");
    ss = new ServerSocket(14);
    //ss = new ServerSocket( sc.nextInt() );  // instantiam ServerSocket-ul la portul citit de la tastatura 
    System.out.println("Serverul a pornit");

    while (true) { // Blocam firul principal al serverului cu un lool care asteapta 
        // conexiuni de la clienti si pe fiecare iteratie face urmatoarele:
      cs = ss.accept(); // serverul asteaptea urmatoare conexiune de la un socket, iar cand aceasta se intampla
      // stocheaza valoarea in obiectul cs, de tip socket, pe server
      sockete.add(new DataOutputStream(cs.getOutputStream())); // dupa conexiune adaugam DataOutputStream-ul
      // al socket-ului abea conectat, in lista noastra din server
      System.out.println("\nClient nou. ");
      new Conexiune(cs,++i,sockete, listNames); // deschidem un thread nou pentru clientul conectat unde dam ca parametrul
      // socket-ul abea conectat, indicele sau si lista cu ceilalti clienti conectati
    }
  }
  
}

class Conexiune extends Thread { // Clasa Conexiune extinde Thread si presupune un proces ce va rula in paralel
    // cu cel principal al serverului, si se va ocupa de primirea si trimiterea mesajelor ce tin
    // de UN SINGUR client ( astfel mai jos regasim variabilele ce ar tine de socket-ul unui client )
    // si lista cu toate DataOutputStreamurile clientilor conectat - > pentru a le putea transmite mesajele mai departe
  int identitate; Socket cs = null; DataInputStream is = null;  DataOutputStream os =null  ;
  ArrayList<DataOutputStream> _sockete;
  ArrayList<String> _listNames;
  public Conexiune(Socket client, int i, ArrayList<DataOutputStream> sockete, ArrayList<String> listNames) // constructorul clasei in care primim parametrii
           throws IOException {    // pe care i-am trimis mai sus
    cs = client; identitate = i;  // atribui socket-ul primit de la server celui local threadu-ului, si identitatea
    is = new DataInputStream(cs.getInputStream());  // fluxul de intrare
    os = new DataOutputStream(cs.getOutputStream());// cel de iesire
    this._sockete = sockete;  // si lista de clienti conectati la server
    this._listNames = listNames;
    
    start(); // pornim threadu-ul
  }

  public void run() { // metoda ce se apleaza la pornirea threadului
    try {
       while (true) { // blocam firul threadului cu un loop ce primeste mesaje de la client
                String message = is.readUTF(); // citim un mesaj prin fluxul de intrare
                System.out.println(message);  // il afisam
                if(message.startsWith("/nick"))
                {              
                    String[] temp = message.split(" ");
                    String nume = temp[1];
                    System.out.println(nume);
                    
                    if(_listNames.contains(nume))
                    {
                        os.writeUTF("Nickname deja exista. Alege altul!");
                    }
                    else
                    {
                        _listNames.add(nume);
                        os.writeUTF("Nick accepatat!");
                    }
                }
                else if(message.startsWith("/private"))
                {
                    String strPrivateName = message.split("44")[2];
                    String strPrivateMessage = message.split("44")[3];
                    String strSenderName = message.split("44")[1];
                    System.out.println("Raw message: "+ message + " sender name: " + strSenderName 
                            + "and recepient: "+ strPrivateName + " and the message :" + strPrivateMessage);
                    int nIndex = _listNames.indexOf(strPrivateName);
                    _sockete.get(nIndex).writeUTF(strSenderName + ": " + strPrivateMessage);
                    
                }
                else if(message.startsWith("/mesaj"))
                {
                    System.out.println("Raw message from client" + message);
                    String[] temp = message.split("44");
                    System.out.println("parsed: " + temp[2]);
                    boolean isTrue ;
                    for(int i=0;i<_sockete.size();i++) // apoi parcurgem lista de clienti conectati la server si le trimitem                   
                    {
                        isTrue = temp[1].equals(_listNames.get(i));
                        System.out.println("Numele din mesaj: " + temp[1]+ " si celui caruia i se atribuie: " 
                                + _listNames.get(i) + " si val de adev a expresiei din if: " + isTrue);
                        if(!temp[1].equals(_listNames.get(i)))
                        _sockete.get(i).writeUTF(temp[1] + ": " + temp[2]);    // mesajul ce tocmai a fost primit
                        //TO DO: In caz ca nu e ok cu afisarea, baga aici afisare, fara nume
                    }
                }
            }
    }
    catch(Exception e) { }
  }
}