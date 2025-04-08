import javafx.application.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.*;
import java.util.Random;
 
public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    int bombCount = 0;
    int boardSize = 0;
    @Override
    public void start(Stage primaryStage) {
///STAGE EARLY SETUP
        
        Stage gameSettings = new Stage();
        Stage rules = new Stage();
        Stage gameStage = new Stage();
        rules.setX(875);
        gameSettings.initStyle(StageStyle.UNDECORATED);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        /*gameStage.initStyle(StageStyle.UNDECORATED);*/
//MAIN MENU
        BorderPane root = new BorderPane();
        VBox centerPane = new VBox();
        Label title = new Label("5D Minesweeper");
        title.setStyle("-fx-font-size: 30px;"+"-fx-font-family: 'Verdana';");
        Label startText = new Label("Start");
        startText.setOnMouseClicked(e->{
            primaryStage.hide();
            gameSettings.show();
        });
        Label rulesLink = new Label("Rules");
        rulesLink.setOnMouseClicked(e->{
            rules.show();
        });
        Label cls = new Label("Exit");
        cls.setOnMouseClicked(e->{
            primaryStage.close();
            rules.close();
        });
        centerPane.getChildren().addAll(title,startText,rulesLink,cls);
        centerPane.setAlignment(Pos.TOP_CENTER);
        root.setCenter(centerPane);
        
        primaryStage.setScene(new Scene(root, 350, 100));
        primaryStage.show();

//SETTINGS
        ///Back Button
        BorderPane settingsRoot = new BorderPane();
        gameSettings.setScene(new Scene(settingsRoot, 350, 135));
        HBox settingsTop = new HBox();
        Label back = new Label("<-");
        back.setOnMouseClicked(e->{
            primaryStage.show();
            gameSettings.hide();
        });
        settingsTop.getChildren().addAll(back);
        settingsRoot.setTop(settingsTop);
        ///Main Settings
        GridPane settingsCenter = new GridPane();
        settingsCenter.setHgap(5);
        settingsCenter.setVgap(5);
        settingsCenter.add(new Label("Difficulty: "),0,0);
        ComboBox<String> difSetBox = new ComboBox<>();
        difSetBox.getItems().addAll("Easy", "Medium", "Hard", "Impossible", "Custom");
        settingsCenter.add(difSetBox,1,0);
        //Presets and Actual Settings
        TextField BSTF = new TextField();
        BSTF.setText("");
        TextField BCTF = new TextField();
        BCTF.setText("");
        difSetBox.setOnAction(e->{
            if (difSetBox.getValue() == "Easy"){
                BSTF.setText("3");
                BCTF.setText("1");
            }else if (difSetBox.getValue() == "Medium"){
                BSTF.setText("5");
                BCTF.setText("5");
            }else if (difSetBox.getValue() == "Hard"){
                BSTF.setText("7");
                BCTF.setText("20");
            }else if (difSetBox.getValue() == "Impossible"){
                BSTF.setText("10");
                BCTF.setText("50");
            }
        });
        BSTF.setOnKeyReleased(e->{
            if (!(e.getText().charAt(0) > 47 && e.getText().charAt(0) < 58)){
                BSTF.setText("");
            }
            difSetBox.setValue("Custom");
        });
        BCTF.setOnKeyReleased(e->{
            if (!(e.getText().charAt(0) > 47 && e.getText().charAt(0) < 58)){
                BCTF.setText("");
            }
            
            difSetBox.setValue("Custom");
        });
        Label startButton = new Label("Start");
//RULES
        TextArea rulesTA = new TextArea();
        rulesTA.setWrapText(true);
        rulesTA.setEditable(false);
        rulesTA.setText("Dummy Text");
        rulesTA.setPrefHeight(700);
        rulesTA.setPrefWidth(330);
        ScrollPane rulesRoot = new ScrollPane(rulesTA);
        rules.setScene(new Scene(rulesRoot, 350,350));    
        settingsCenter.add(new Label("Board Size (All sides are equal): "),0,1);
        settingsCenter.add(new Label("Bomb Count:" ),0,2);
        settingsCenter.add(BSTF, 1,1);
        settingsCenter.add(BCTF,1,2);
        settingsCenter.add(startButton, 0,3);
        settingsRoot.setCenter(settingsCenter);    
        startButton.setOnMouseClicked(e->{
            int hi = Integer.parseInt(BSTF.getText());
            int bye = Integer.parseInt(BCTF.getText());
            if (hi > 0 && hi <= 10 && bye > 0 && bye <= hi*hi*hi*hi*hi){
                gameSettings.hide();
                gameStage.show();
            }
//THE GAME
            bombCount = bye;
            boardSize = hi;
            Pane gameRoot = new Pane();
            int squareWidth = 20;
            gameStage.setScene(new Scene(gameRoot, squareWidth*boardSize*boardSize*1.23,squareWidth*boardSize*boardSize*1.23));
            gameStage.setX(600);
            gameStage.setY(250);
            int x = -squareWidth*boardSize;
            int y = -squareWidth*boardSize;
            int counter = 0;
            Rectangle[][][][][] squareContainer = new Rectangle[5][5][5][5][5];
            for (int i = 0; i<boardSize; i++){
                for (int j = 0; j<boardSize; j++){
                    y += squareWidth*boardSize*1.05;
                    for (int k = 0; k<boardSize; k++){
                        x += squareWidth*boardSize*1.05;
                        for (int l = 0; l<boardSize; l++){
                            y+=squareWidth;
                            for (int m = 0; m<boardSize; m++){
                                x+=squareWidth;
                                squareContainer[i][j][k][l][m] = new Rectangle(x,y,squareWidth,squareWidth);
                                squareContainer[i][j][k][l][m].setFill(Color.WHITE);
                                squareContainer[i][j][k][l][m].setStroke(Color.BLACK);
                                gameRoot.getChildren().add(squareContainer[i][j][k][l][m]);
                                counter ++;
                                System.out.println(counter);
                            }
                            x-=squareWidth*boardSize;
                        }
                        y-=squareWidth*boardSize;
                    }
                    x-=squareWidth*boardSize*boardSize*1.05;
                }
                y -=squareWidth*boardSize*boardSize*1.05;
            }
            boolean onward = false;
            Random random = new Random();
            int[] bomb1 = new int[3124];
            int[] bomb2 = new int[3124];
            int[] bomb3 = new int[3124];
            int[] bomb4 = new int[3124];
            int[] bomb5 = new int[3124];
            while (!onward){
                System.out.println("try");
                onward = true;
                for (int i = 0; i<bombCount; i++){
                    bomb1[i] = boardSize-1;
                    bomb2[i] = random.nextInt(boardSize);
                    bomb3[i] = random.nextInt(boardSize);
                    bomb4[i] = random.nextInt(boardSize);
                    bomb5[i] = random.nextInt(boardSize);
                    for (int j = 0; j<bombCount; j++){
                        if (!(i == j)){
                            if (bomb1[i] == bomb1[j]){
                                if (bomb2[i] == bomb2[j]){
                                    if (bomb3[i] == bomb3[j]){
                                        if (bomb4[i] == bomb4[j]){
                                            if (bomb5[i] == bomb5[j]){
                                                onward = false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Tile[] bombArray = new Tile[bombCount];
            Tile[][][][][] tileArray = new Tile[5][5][5][5][5];
            for (int i = 0; i<boardSize; i++){
                for (int j = 0; j<boardSize; j++){
                    for (int k = 0; k<boardSize; k++){
                        for (int l = 0; l<boardSize; l++){
                            for (int m = 0; m<boardSize; m++){
                                tileArray[i][j][k][l][m] = new Tile(0, squareContainer[i][j][k][l][m]);
                                for (int n = 0; n<bombCount; n++){
                                    if ((squareContainer[i][j][k][l][m] == squareContainer[bomb1[n]][bomb2[n]][bomb3[n]][bomb4[n]][bomb5[n]])){
                                        squareContainer[i][j][k][l][m].setFill(Color.BLACK);
                                        counter = 0;
                                        tileArray[i][j][k][l][m].isBomb();
                                        tileArray[i-1][j][k][l][m].addBomb();
                                            tileArray[i-1][j-1][k][l][m].addBomb();
                                                tileArray[i-1][j-1][k-1][l][m].addBomb();
                                                    tileArray[i-1][j-1][k-1][l-1][m].addBomb();
                                                        tileArray[i-1][j-1][k-1][l-1][m-1].addBomb();
                                                    tileArray[i-1][j-1][k-1][l][m-1].addBomb();
                                                tileArray[i-1][j-1][k][l-1][m].addBomb();
                                                    tileArray[i-1][j-1][k][l-1][m-1].addBomb();
                                                tileArray[i-1][j-1][k][l][m-1].addBomb();
                                            tileArray[i-1][j][k-1][l][m].addBomb();
                                                tileArray[i-1][j][k-1][l-1][m].addBomb();
                                                    tileArray[i-1][j][k-1][l-1][m-1].addBomb();
                                                tileArray[i-1][j][k-1][l][m-1].addBomb();
                                            tileArray[i-1][j][k][l-1][m].addBomb();
                                                tileArray[i-1][j][k][l-1][m-1].addBomb();
                                            tileArray[i-1][j][k][l][m-1].addBomb();
                                        tileArray[i][j-1][k][l][m].addBomb();
                                            tileArray[i][j-1][k-1][l][m].addBomb();
                                                tileArray[i][j-1][k-1][l-1][m].addBomb();
                                                    tileArray[i][j-1][k-1][l-1][m-1].addBomb();
                                                tileArray[i][j-1][k-1][l][m-1].addBomb();
                                            tileArray[i][j-1][k][l-1][m].addBomb();
                                                tileArray[i][j-1][k][l-1][m-1].addBomb();
                                            tileArray[i][j-1][k][l][m-1].addBomb();
                                        tileArray[i][j][k-1][l][m].addBomb();
                                            tileArray[i][j][k-1][l-1][m].addBomb();
                                                tileArray[i][j][k-1][l-1][m-1].addBomb();
                                            tileArray[i][j][k-1][l][m-1].addBomb();
                                        tileArray[i][j][k][l-1][m].addBomb();
                                            tileArray[i][j][k][l-1][m-1].addBomb();
                                        tileArray[i][j][k][l][m-1].addBomb();
                                        
                                        
                                        
                                    }
                                }
                            }
                        }
                    }
                }
            }
            });
            
    }
}
class Tile{
    int bombs = -2;
    Rectangle square;
    Tile(int bombs, Rectangle square){
        this.bombs = bombs;
        this.square = square;
    }
    public int getBombs(){
        return this.bombs;
    }
    public Rectangle getSquare(){
        return this.square;
    }
    public void addBomb(){
        this.bombs ++;
    }
    public void isBomb(){
        this.bombs = -1;
    }
}
