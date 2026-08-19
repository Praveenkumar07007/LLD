package projects.googledoc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class DocumentEditor {
  private List<String> documentElements;
  private String renderedDocument;

  public DocumentEditor(){
    documentElements = new ArrayList<>();
    renderedDocument = "";
  }

  public void addText(String text){
    documentElements.add(text);
  }

  public void addImage(String path){
    documentElements.add(path);
  }

  public String renderDocument() {
    if (renderedDocument.isEmpty()) {
      StringBuilder result = new StringBuilder();
      for (String element : documentElements) {
        if (element.length() > 4 &&
                (element.endsWith(".jpg") || element.endsWith(".png"))) {
          result.append("[Image: ").append(element).append("]\n");
        } else {
          result.append(element).append("\n");
        }
      }
      renderedDocument = result.toString();
    }
    return renderedDocument;
  }

  public void saveToFile(){
    try {
      FileWriter writer = new FileWriter("document.txt");
      writer.write(renderDocument());
      writer.close();
      System.out.println("docement save to document.txt");
    } catch(IOException e){
      System.out.println("unable to open file for writing");
    }
  }
}

public class GoogleDoc{
  public static void main(String args[]){
    DocumentEditor editor = new DocumentEditor();
    editor.addText("hello");
    editor.addText("hey");
    editor.addText("hi");
    System.out.println(editor.renderDocument());
    editor.saveToFile();
  }
}
