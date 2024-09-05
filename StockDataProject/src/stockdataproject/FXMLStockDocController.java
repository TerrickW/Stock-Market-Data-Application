package stockdataproject;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FXMLStockDocController implements Initializable {

    @FXML
    private Button searchButtonID;
    @FXML
    private TextField searchedStockID;
    @FXML
    private TextArea lastSearchedID;
    @FXML
    private TextArea currentPriceID;
    @FXML
    private Button resetButtonID;
    private TextArea recentNewsID;

    private List<String> searchHistory = new ArrayList<>();
    @FXML
    private Hyperlink hyperLinkID1;
    @FXML
    private Hyperlink hyperLinkID2;
    @FXML
    private Hyperlink hyperLinkID3;
    @FXML
    private Hyperlink hyperLinkID4;
    @FXML
    private TextArea statID;
    @FXML
    private Button buyButton;
    @FXML
    private ImageView buyImageID;
    private Image buyImage;
    @FXML
    private Button sellID;
    @FXML
    private ImageView sellImageID;
    private Image sellImage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
                buyImage = new Image(getClass().getResourceAsStream("/stockdataproject/pictures/buy.png"));
                buyImageID.setImage(buyImage);
                
                sellImage = new Image(getClass().getResourceAsStream("/stockdataproject/pictures/sell.png"));
                sellImageID.setImage(sellImage);

        
        
        
        
        

        resetButtonID.setOnAction((ActionEvent e) -> resetSearch());

        if (searchButtonID != null) {
            searchButtonID.setOnAction((ActionEvent event) -> {
                try {
                    String search = searchedStockID.getText();
                    if (!search.isEmpty()) {
                        addSearchToHistory(search);
                        updateUIWithSearch(search);
                        showDataStats(search);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(FXMLStockDocController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } else {
            System.err.println("searchButtonID is null");
        }

    }

    private void addSearchToHistory(String search) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MMM-dd h:mm a");
        String formattedTime = now.format(formatter);
        String searchEntry = search.toUpperCase() + " @ " + formattedTime;

        searchHistory.add(0, searchEntry);

        if (searchHistory.size() > 10) {
            searchHistory.remove(searchHistory.size() - 1);
        }
    }

    private void updateUIWithSearch(String search) throws IOException {
        setTextArea(lastSearchedID, search);
        setTextAreaCurrentPrice(currentPriceID, search);
        setTextAreaNews(search);
    }

    private void setTextArea(TextArea searchTextArea, String lastSearch) {
        StringBuilder historyText = new StringBuilder("Search History:\n");

        for (String entry : searchHistory) {
            historyText.append(entry).append("\n");
        }

        searchTextArea.setText(historyText.toString());
    }

    private void setTextAreaCurrentPrice(TextArea priceTextArea, String symbol) throws IOException {
        String url = "https://finance.yahoo.com/quote/" + symbol;
        Document doc = Jsoup.connect(url).get();
        Elements priceElements = doc.select(".livePrice");

        if (priceElements.isEmpty()) {
            priceTextArea.setText("That symbol does not exist");
            hyperLinkID1.setText("No news available for this symbol");
            hyperLinkID1.setOnAction((ActionEvent e) -> hyperLinkID1.setText("Article 1: There exist no Link"));

            hyperLinkID2.setText("No news available for this symbol");
            hyperLinkID2.setOnAction((ActionEvent e) -> hyperLinkID2.setText("Article 2: There exist no Link"));

            hyperLinkID3.setText("No news available for this symbol");
            hyperLinkID3.setOnAction((ActionEvent e) -> hyperLinkID3.setText("Article 3: There exist no Link"));

            hyperLinkID4.setText("No news available for this symbol");
            hyperLinkID4.setOnAction((ActionEvent e) -> hyperLinkID4.setText("Article 4: There exist no Link"));

            statID.setText("Statistics and Data does not exist");

        } else {
            priceTextArea.setText(priceElements.text());
        }
    }

    private void setTextAreaNews(String symbol) throws IOException {
        String url = "https://www.cnbc.com/quotes/" + symbol;
        Document doc = Jsoup.connect(url).get();
        Elements newsElements = doc.select("a.LatestNews-headline"); // Select all news article links

        if (newsElements.isEmpty()) {
            hyperLinkID1.setText("No news available for this symbol");
            hyperLinkID2.setText("No news available for this symbol");
            hyperLinkID3.setText("No news available for this symbol");
            hyperLinkID4.setText("No news available for this symbol");

        } else {
            int count = 0; // To count how many articles we have processed

            for (Element newsElement : newsElements) {
                if (count >= 4) {
                    break; // Limit to 4 articles
                }
                String link = newsElement.attr("href");
                String title = newsElement.text();

                switch (count) {
                    case 0:
                        hyperLinkID1.setText("Article 1: " + title);
                        hyperLinkID1.setOnAction((ActionEvent e) -> showPopupWindow(link));
                        break;
                    case 1:
                        hyperLinkID2.setText("Article 2: " + title);
                        hyperLinkID2.setOnAction((ActionEvent e) -> showPopupWindow(link));
                        break;
                    case 2:
                        hyperLinkID3.setText("Article 3: " + title);
                        hyperLinkID3.setOnAction((ActionEvent e) -> showPopupWindow(link));
                        break;
                    case 3:
                        hyperLinkID4.setText("Article 4: " + title);
                        hyperLinkID4.setOnAction((ActionEvent e) -> showPopupWindow(link));
                        break;
                }

                count++;
            }

        }
    }

    private void resetSearch() {
        System.out.println("Reset Button Has Been Pressed");
        searchHistory.clear();
        setTextArea(lastSearchedID, "");
        currentPriceID.clear();
        searchedStockID.clear();
        hyperLinkID1.setText("Article 1: ");
        hyperLinkID1.setOnAction((ActionEvent e) -> hyperLinkID1.setText("Article 1: There exist no Link"));
        hyperLinkID2.setText("Article 2: ");
        hyperLinkID2.setOnAction((ActionEvent e) -> hyperLinkID2.setText("Article 2: There exist no Link"));
        hyperLinkID3.setText("Article 3: ");
        hyperLinkID3.setOnAction((ActionEvent e) -> hyperLinkID3.setText("Article 3: There exist no Link"));
        hyperLinkID4.setText("Article 4: ");
        hyperLinkID4.setOnAction((ActionEvent e) -> hyperLinkID4.setText("Article 4: There exist no Link"));
        statID.clear();

        /*
        private Hyperlink hyperLinkID1;
    @FXML
    private Hyperlink hyperLinkID2;
    @FXML
    private Hyperlink hyperLinkID3;
    @FXML
    private Hyperlink hyperLinkID4;
         */
    }

    private void showPopupWindow(String url) {
        // Create a new Stage (window)
        Stage popupStage = new Stage();
        popupStage.setTitle("Webpage Viewer");

        // Create a WebView and get its WebEngine
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Load the specified URL in the WebEngine
        webEngine.load(url);

        // Create a BorderPane to hold the WebView
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(webView);

        // Set the scene and show the stage
        Scene scene = new Scene(borderPane, 800, 600); // Set preferred size for the popup window
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void showDataStats(String symbol) throws IOException {
        String url = "https://finance.yahoo.com/quote/" + symbol;
        Document doc = Jsoup.connect(url).get();

        // Select the container with the class "container yf-tx3nkj"
        Element container = doc.selectFirst(".container.yf-tx3nkj");

        if (container != null) {
            Elements statsItems = container.select("li");
            StringBuilder statsText = new StringBuilder();

            for (Element item : statsItems) {
                String label = item.selectFirst(".label") != null ? item.selectFirst(".label").text() : "N/A";
                String value = item.selectFirst(".value") != null ? item.selectFirst(".value").text() : "N/A";
                statsText.append(label).append(": ").append(value).append("\n");
            }

            // Print to console or update a TextArea or other UI component
            statID.setText(statsText.toString());
            //   System.out.println(statsText.toString());
        } else {
            System.out.println("Container with class 'container yf-tx3nkj' not found.");
        }
    }

}

/*
 // Hyperlinks actions
        hyperLinkID1.setOnAction((ActionEvent e) -> {
            showPopupWindow("https://www.google.com");
        });
         hyperLinkID2.setOnAction((ActionEvent e) -> {
            showPopupWindow("https://www.google.com");
        });
         hyperLinkID3.setOnAction((ActionEvent e) -> {
            showPopupWindow("https://www.google.com");
        });
          hyperLinkID4.setOnAction((ActionEvent e) -> {
            showPopupWindow("https://www.google.com");
        });
*/
