package com.company;
import java.io.PrintStream;
import java.io.FileNotFoundException;

import java.nio.ByteBuffer;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

class TmData {
    String name;
    int number, time, dimension, type;
    TmData()
    {
        this.name="";this.number=this.time=this.dimension=this.type=0;
    }
    TmData(String name, int number, int time, int dimension, int type)
    {
        this.name=name;
        this.number=number;
        this.time=time;
        this.dimension=dimension;
        this.type=type;
    }
    TmData(TmData ob)
    {
        this.name=ob.name;
        this.number=ob.number;
        this.time=ob.time;
        this.dimension=ob.dimension;
        this.type=ob.type;
    }
    void print()
    {
        System.out.printf("%s\t%04d\t%06x\t%x\t%x\t", this.name, this.number,this.time,this.dimension,this.type);
    }
    String GetStr()
    {
        String s=String.format("%s\t#%04d\ttime: %08d\ttype: %d\t", this.name,this.number,this.time,this.type);
        return s;
    }
}

class TmLong extends TmData {
    int value;
    TmLong() { super(); this.value=0;}
    TmLong(String name, int number, int time, int dimension, int type, int value)
    {
        super(name, number, time, dimension, type);
        this.value=value;
    }
    TmLong(TmLong ob)
    {
        super(ob.name, ob.number, ob.time, ob.dimension, ob.type);
        this.value=ob.value;
    }
    void print()
    {
        super.print();
        System.out.printf("%d\n", this.value);
    }
    String GetStr()
    {
        String s1=super.GetStr();
        String s2=s1+String.format("value: %d", this.value);
        return s2;
    }
}

class TmDouble extends TmData {
    double value;
    TmDouble(){super(); this.value=0;}
    TmDouble(String name, int number, int time, int dimension, int type, double value)
    {
        super(name, number, time, dimension, type);
        this.value=value;
    }
    TmDouble(TmDouble ob)
    {
        super(ob.name, ob.number, ob.time, ob.dimension, ob.type);
        this.value=ob.value;
    }
    void print()
    {
        super.print();
        System.out.printf("%f\n", this.value);
    }
    String GetStr()
    {
        String s1=super.GetStr();
        String s2=s1+String.format("value: %f", this.value);
        return s2;
    }
}

class TmCode extends TmData {
    int len;
    int code;
    TmCode(){super(); this.len=0; this.code=0;}
    TmCode(String name, int number, int time, int dimension, int type, int len, int code)
    {
        super(name, number, time, dimension, type);
        this.len=len;
        this.code=code;
    }
    TmCode(TmCode ob)
    {
        super(ob.name, ob.number, ob.time, ob.dimension, ob.type);
        this.len=ob.len;
        this.code=ob.code;
    }
    void print()
    {
        super.print();
        System.out.printf("%d\t%d", this.len, this.code);
    }
    String GetStr()
    {
        String s1=super.GetStr();
        String s2=s1+String.format("length: %d\tcode: %s", this.len, String.format("%"+len+"s", Integer.toBinaryString(code)).replace(' ', '0'));
        return s2;
    }
}

class TmPoint extends TmData {
    int len, size;
    byte []sequence;
    TmPoint(){super(); this.len=0; this.size=0; this.sequence=null;}
    TmPoint(String name, int number, int time, int dimension, int type, int size, int len, byte []sequence)
    {
        super(name, number, time, dimension, type);
        this.len=len;
        this.size=size;
        this.sequence=new byte[len];
        for(int i=0; i<this.len; i++)
        {
            this.sequence[i]=sequence[i];
        }
    }
    TmPoint(TmPoint ob)
    {
        super(ob.name, ob.number, ob.time, ob.dimension, ob.type);
        this.len=ob.len;
        this.size=ob.size;
        this.sequence=new byte[ob.len];
        for(int i=0; i<ob.len; i++)
        {
            this.sequence[i]=ob.sequence[i];
        }
    }
    void print()
    {
        super.print();
        System.out.printf("%d\t%d\t", this.size, this.len);
        for(int i=0; i<this.len; i++)
        {
            System.out.printf("%d\t", this.sequence[i]);
        }
        System.out.printf("\n");
    }
    String GetStr()
    {
        String s1=super.GetStr();
        /*String s2=s1+String.format("%d\t%d\t", this.size, this.len);
        for(int i=0; i<this.len; i++)
        {
            s2=s2+String.format("%d\t", this.sequence[i]);
        }*/
        return s1;
    }
}

class Config {
    String file_input_dat_xml;
    String file_output_dat_xml;
    String file_input_knp;
    String file_output_knp;
    String config_xml;
    Config(String xmlfile) throws ParserConfigurationException, SAXException {
        this.config_xml=xmlfile;
        Document doc;
        try (FileInputStream fin = new FileInputStream(xmlfile)) {
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = parser.parse(fin);
            final NodeList topNodes = doc.getElementsByTagName("xmlfile");
            for (int i = 0; i < topNodes.getLength(); i++) {
                final Node node = topNodes.item(i);
                if (!(node instanceof Element))
                    continue;
                final Element element = (Element) node;
                if ("xmlfile".equals(element.getTagName())) {
                    this.file_input_dat_xml = element.getAttribute("inputFile");
                    this.file_output_dat_xml = element.getAttribute("outputFile");
                }
            }
            final NodeList topNodes2 = doc.getElementsByTagName("tmfile");
            for (int i = 0; i < topNodes2.getLength(); i++) {
                final Node node2 = topNodes2.item(i);
                if (!(node2 instanceof Element))
                    continue;
                final Element element2 = (Element) node2;
                if ("tmfile".equals(element2.getTagName())) {
                    this.file_input_knp = element2.getAttribute("inputFile");
                    this.file_output_knp = element2.getAttribute("outputFile");
                }
            }
        } catch (IOException ex) {
            System.out.println("No file had been found!!!");
        }
    }
    void Print() {
        System.out.println(config_xml);
        System.out.println("file input dat xml: "+file_input_dat_xml);
        System.out.println("file output dat xml: "+file_output_dat_xml);
        System.out.println("file input knp: "+file_input_knp);
        System.out.println("file output knp: "+file_output_knp);
    }
}

class Dim {
    ArrayList<String> dimens=new ArrayList();
    Dim(String filename) throws FileNotFoundException {
        Scanner in = new Scanner(new File(filename), "Windows-1251");
        while (in.hasNextLine())
            dimens.add(in.nextLine());
    }
    void Print() {
        for (int i=0;i<dimens.size();i++)
            System.out.println(dimens.get(i));
    }
    String GetString(int key) {
        return dimens.get(key-1);//-1
    }
}

class DatXML {
    HashMap<String, String> data_mass = new HashMap<>(); // ArrayList<String> number_mass=new ArrayList<>();
    DatXML(String xmlfile) throws ParserConfigurationException, SAXException {
        Document doc;
        try (FileInputStream fin = new FileInputStream(xmlfile)) {
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = parser.parse(fin);
            NodeList topNodes = doc.getElementsByTagName("Param");
            for (int i = 0; i < topNodes.getLength(); i++) {
                Node node = topNodes.item(i);
                if (!(node instanceof Element))
                    continue;
                Element element = (Element) node;
                if ("Param".equals(element.getTagName())) {
                    data_mass.put(element.getAttribute("number"), element.getAttribute("name"));
                }
            }
        } catch (IOException ex) {
            System.out.println("No file had been found!!!");
        }
    }
    void PrintKey(String key) {
        System.out.print(data_mass.get(key) + " ");
    }
    String GetString(String key) {
        return data_mass.get(key);
    }
}

class SortByNumber implements Comparator<TmData> {
    public int compare(TmData a, TmData b) {
        return a.number - b.number;
    }
}

class SortByName implements Comparator<TmData> {
    public int compare(TmData a, TmData b) {
        return a.name.compareTo(b.name);
    }
}

class Main {
    public static ArrayList<TmData> TM = new ArrayList<>();

    public static int CountTM(String file_input_knp) {
        int number, Count = 0;
        int type, length;
        byte i[] = new byte[16];
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file_input_knp);
            fin.skip(32);
            while ((fin.read(i)) > 0) {
                number = (i[0] << 8 & 0xFF00 | i[1] & 0xFF);
                if (number == 0xFFFF)
                    continue;
                type = (i[7] & 0xf);
                if (type == 3) {
                    length = (i[10] << 8 & 0xFF00 | i[11] & 0XFF);
                    if (length > 4)
                        fin.skip(length - 4);
                }
                Count++;
            }
            return Count;
        } catch (FileNotFoundException exc) {
            System.out.println("Файл не найден.");
        } catch (IOException exc) {
            System.out.println("Ошибка ввода-вывода");
        } finally {
            try {
                if (fin != null) fin.close();
            } catch (IOException exc) {
                System.out.println("Ошибка при закрытии файла");
            }
        }
        return -1;
    }   //Лаба №4

    public static void PrintTM(String file_input_knp) {
        int k = 0;
        byte i[] = new byte[16];
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file_input_knp);
            System.out.printf("Open file [OK]\n");
            while ((fin.read(i)) > 0) {
                System.out.printf("%06x\t", k);
                k = k + 16;
                for (int j = 0; j < 16; j++) {
                    if (i[j] > 15 | i[j] < 0) System.out.printf("%x ", i[j]);
                    if (i[j] < 16 & i[j] >= 0) System.out.printf("0%x ", i[j]);
                    if (j == 7) System.out.printf("\t");
                }
                System.out.printf("\n");
            }
        } catch (FileNotFoundException exc) {
            System.out.println("Файл не найден.");
        } catch (IOException exc) {
            System.out.println("Ошибка ввода-вывода");
        } finally {
            try {
                if (fin != null) fin.close();
                System.out.println("File close [OK]");
            } catch (IOException exc) {
                System.out.println("Ошибка при закрытии файла");
            }
        }
    }  //Лаба №3

    public static void LoadTM(ArrayList<TmData> TM, int c, String file_input_knp, DatXML name_number){
        System.out.println("Start Load TM");
        int number, time, dimension, type, value, code, len, size, j=0;
        String name;
        byte i[] = new byte[16];
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file_input_knp);
            fin.skip(32);
            while ((fin.read(i)) > 0) {
                number = (i[0] << 8 & 0xFF00 | i[1] & 0xFF);
                name = name_number.GetString(Integer.toString(number));
                time=(i[2]<<24 & 0xFF000000 | i[3]<<16 & 0xFF0000 | i[4]<<8 & 0xFF00 | i[5] & 0xFF);
                dimension=(i[6]);
                if (dimension<0) dimension+=256;
                if (number == 0xFFFF)
                    continue;
                type = (i[7] & 0xf);
                switch(type)
                {
                    case 0:     //long
                        value=(i[12] << 16 & 0xFF000000 | i[13] & 0xFF0000 | i[14] & 0xFF00 | i[15] & 0xFF);
                        TM.add(new TmLong(name, number, time, dimension, type, value));
                        break;
                    case 1:     //double
                        double v=java.nio.ByteBuffer.wrap(new byte[]{i[8],i[9],i[10],i[11],i[12],i[13],i[14],i[15]}).getDouble();
                        TM.add(new TmDouble(name, number, time, dimension, type, v));
                        break;
                    case 2:     //code
                        len=(i[10] << 8 & 0xFF00 | i[11] & 0xFF);
                        code= ByteBuffer.wrap(i,12,4).getInt();
                        TM.add(new TmCode(name, number, time, dimension, type, len, code));
                        break;
                    case 3:     //point
                        len=(i[10] << 8 & 0xFF00 | i[11] & 0xFF);
                        size=(i[8] << 8 & 0xFF00 | i[9] & 0xFF);
                        byte sequence[] = new byte[len];
                        if (len > 4) {
                            byte s1[]=new byte[4];
                            byte s2[]=new byte[len-4];
                            for(int o=0; o<4;o++)
                                s1[o]=i[12+o];
                            fin.read(s2);
                            for(int o=0;o<4; o++)
                                sequence[o]=s1[o];
                            for(int o=0;o<len-4; o++)
                                sequence[o+4]=s2[o];
                        }
                        else
                            for(int o=0; o<len;o++)
                                sequence[o]=i[12+o];
                        TM.add(new TmPoint(name, number, time, dimension, type, size, len, sequence));
                        break;
                }
                j++;
            }
        } catch (FileNotFoundException exc) {
            System.out.println("Файл не найден.");
        } catch (IOException exc) {
            System.out.println("Ошибка ввода-вывода");
        } finally {
            try {
                if (fin != null) fin.close();
                System.out.println("Load TM success!");
            } catch (IOException exc) {
                System.out.println("Ошибка при закрытии файла");
            }
        }
    }   //Чтение файла в схему классов

    public static void PrintTMtoFile(ArrayList<TmData> TM, DatXML name_number, Dim dimens, int c, String filename) throws FileNotFoundException {
        PrintStream filePrintStream = new PrintStream(filename);
        for(int i=0; i<TM.size(); i++) {
                filePrintStream.println(TM.get(i).GetStr() + " " + dimens.GetString(TM.get(i).dimension));
        }
    }   //вывод TM записей в файл

    public static void main(String args[]) throws FileNotFoundException {
        try {
            Config config = new Config("config.xml");
            config.Print();
            Dim dimens=new Dim("dimens.ion");
            DatXML name_number=new DatXML("dat.xml");
            int c=CountTM(config.file_input_knp);
            LoadTM(TM,c,config.file_input_knp,name_number);
            PrintTMtoFile(TM, name_number,dimens,c,"res.txt");
            Collections.sort(TM, new SortByName());
            PrintTMtoFile(TM, name_number,dimens,c,"res_sort_by_name.txt");
            Collections.sort(TM, new SortByNumber());
            PrintTMtoFile(TM, name_number,dimens,c,"res_sort_by_number.txt");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}