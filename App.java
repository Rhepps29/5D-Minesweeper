import javafx.application.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.ColorInput;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.*;
import java.util.Random;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.HashSet;

 
public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    int bombCount = 0;
    int boardSize = 0;
    int flagged = 0;
    boolean canSwitch = true;
    @Override
    public void start(Stage primaryStage) {
///STAGE EARLY SETUP
        
        Stage gameSettings = new Stage();
        Stage rules = new Stage();
        Stage gameStage = new Stage();
        rules.setX(875);
        gameSettings.initStyle(StageStyle.UNDECORATED);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        gameStage.initStyle(StageStyle.UNDECORATED);
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
        gameSettings.setScene(new Scene(settingsRoot, 385, 135));
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
                BCTF.setText("3");
            }else if (difSetBox.getValue() == "Medium"){
                BSTF.setText("4");
                BCTF.setText("10");
            }else if (difSetBox.getValue() == "Hard"){
                BSTF.setText("5");
                BCTF.setText("30");
            }else if (difSetBox.getValue() == "Impossible"){
                BSTF.setText("5");
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
        startButton.setTextAlignment(TextAlignment.RIGHT);
//RULES
        TextArea rulesTA = new TextArea();
        rulesTA.setWrapText(true);
        rulesTA.setEditable(false);
        rulesTA.setText("Rules:\nEach tile contains a number. That number displays how many bombs are adjacent to that square.\n\nAdjacent squares are:\n"+
        "The surrounding tiles (including the corners)\nThe surrounding tiles and actual tile of the surrounding boxes (including corners)\nThe surrounding tiles of above layers and below layers, including those in adjacent boxes.\n\n"+
        "In order to go to a different layer, press up and down on your keyboard. To mark a mine, right click. To clear a tile, left click.\n\nAll other rules are the same as normal minesweeper.");
        rulesTA.setPrefHeight(300);
        rulesTA.setPrefWidth(330);
        ScrollPane rulesRoot = new ScrollPane(rulesTA);
        rules.setScene(new Scene(rulesRoot, 332,302));    
        settingsCenter.add(new Label("Board Size (All sides are equal): "),0,1);
        settingsCenter.add(new Label("Bomb Count:" ),0,2);
        settingsCenter.add(BSTF, 1,1);
        settingsCenter.add(BCTF,1,2);
        settingsCenter.add(startButton, 2,3);
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
            int squareWidth = 20;
            Pane[] gameRoot = new Pane[5];
            Scene[] gameScene = new Scene[5];
            
            Label bombLabel[] = new Label[5];
            Label exitLabel[] = new Label[5];
            for (int i = 0; i < 5; i++){
                gameRoot[i] = new Pane();
                gameRoot[i].getChildren().add(new Label("Level: "+(i+1)+""));
                bombLabel[i] = new Label();
                bombLabel[i].setLayoutX(50);
                bombLabel[i].setText("Unflagged Bombs: " + (bombCount-flagged)+"/"+(bombCount));
                gameRoot[i].getChildren().add(bombLabel[i]);
                exitLabel[i] = new Label("Exit");
                exitLabel[i].setLayoutX(boardSize*boardSize*squareWidth*1.3-25);
                exitLabel[i].setOnMouseClicked(f->{
                    gameStage.close();
                    primaryStage.show();
                });
                gameRoot[i].getChildren().add(exitLabel[i]);
                gameScene[i] = new Scene(gameRoot[i], boardSize*boardSize*squareWidth*1.3,boardSize*boardSize*squareWidth*1.3);
            }
            gameStage.setScene(gameScene[0]);
            gameStage.setX(1366/2-((boardSize*boardSize*squareWidth*1.3)/2));
            gameStage.setY(768/2-((boardSize*boardSize*squareWidth*1.3)/2)-50);
            gameRoot[0].requestFocus();
            int x = -squareWidth*boardSize;
            int y = -squareWidth*boardSize;
            if (boardSize > 1){
                gameRoot[0].setOnKeyPressed(f->{
                    if (canSwitch && f.getCode() == KeyCode.UP){
                        canSwitch = false;
                        gameStage.setScene(gameScene[1]);
                        gameRoot[1].requestFocus();
                        PauseTransition cooldown = new PauseTransition(Duration.millis(300)); 
                        cooldown.setOnFinished(ev -> canSwitch = true);
                        cooldown.play();
                    }   
                });
            }
            if (boardSize > 2){
                gameRoot[1].setOnKeyPressed(f->{
                    f.getCode();
                    if (canSwitch && f.getCode() == KeyCode.UP){
                        canSwitch = false;
                        gameStage.setScene(gameScene[2]);
                        gameRoot[2].requestFocus();
                        PauseTransition cooldown = new PauseTransition(Duration.millis(300)); 
                        cooldown.setOnFinished(ev -> canSwitch = true);
                        cooldown.play();
                    } 
                    if (canSwitch && f.getCode() == KeyCode.DOWN){
                        canSwitch = false;
                        gameStage.setScene(gameScene[0]);
                        gameRoot[0].requestFocus();
                        PauseTransition cooldown = new PauseTransition(Duration.millis(300)); 
                        cooldown.setOnFinished(ev -> canSwitch = true);
                        cooldown.play();
                    }   
                });
            }else{
                gameRoot[1].setOnKeyPressed(f->{
                    f.getCode();
                    if (canSwitch && f.getCode() == KeyCode.DOWN){
                        canSwitch = false;
                        gameStage.setScene(gameScene[0]);
                        gameRoot[0].requestFocus();
                        PauseTransition cooldown = new PauseTransition(Duration.millis(300)); 
                        cooldown.setOnFinished(ev -> canSwitch = true);
                        cooldown.play();
                    }   
                });
            }
            if (boardSize > 3){
                gameRoot[2].setOnKeyPressed(f->{
                    if ( canSwitch && f.getCode() == KeyCode.UP){
                        canSwitch = false;
                        gameStage.setScene(gameScene[3]);
                        gameRoot[3].requestFocus();
                        PauseTransition cooldown = new PauseTransition(Duration.millis(300)); 
                        cooldown.setOnFinished(ev -> canSwitch = true);
                        cooldown.play();
                    } 
                    if (canSwitch && f.getCode() == KeyCode.DOWN){
                        canSwitch = false;
                        gameStage.setScene(gameScene[1]);
                        gameRoot[1].requestFocus();
                        PauseTransition cooldown = new PauseTransition(Duration.millis(300)); 
                        cooldown.setOnFinished(ev -> canSwitch = true);
                        cooldown.play();
                    }   
                });
            }else{
                gameRoot[2].setOnKeyPressed(f->{
                    f.getCode();
                    if (canSwitch && f.getCode() == KeyCode.DOWN){
                        canSwitch = false;
                        gameStage.setScene(gameScene[1]);
                        gameRoot[1].requestFocus();
                        PauseTransition cooldown = new PauseTransition(Duration.millis(300)); 
                        cooldown.setOnFinished(ev -> canSwitch = true);
                        cooldown.play();
                    }   
                });
            }
            if (boardSize > 4){
                gameRoot[3].setOnKeyPressed(f->{
                    f.getCode();
                    if (canSwitch && f.getCode() == KeyCode.UP){
                        canSwitch = false;
                        gameStage.setScene(gameScene[4]);
                        gameRoot[4].requestFocus();
                        PauseTransition cooldown = new PauseTransition(Duration.millis(300)); 
                        cooldown.setOnFinished(ev -> canSwitch = true);
                        cooldown.play();
                    } 
                    if (canSwitch && f.getCode() == KeyCode.DOWN){
                        canSwitch = false;
                        gameStage.setScene(gameScene[2]);
                        gameRoot[2].requestFocus();
                        PauseTransition cooldown = new PauseTransition(Duration.millis(300)); 
                        cooldown.setOnFinished(ev -> canSwitch = true);
                        cooldown.play();
                    }   
                });
                gameRoot[4].setOnKeyPressed(f->{
                    f.getCode();
                    if (canSwitch && f.getCode() == KeyCode.DOWN){
                        canSwitch = false;
                        gameStage.setScene(gameScene[3]);
                        gameRoot[3].requestFocus();
                        PauseTransition cooldown = new PauseTransition(Duration.millis(300)); 
                        cooldown.setOnFinished(ev -> canSwitch = true);
                        cooldown.play();
                    }   
                });
            }else{
                gameRoot[3].setOnKeyPressed(f->{
                    f.getCode();
                    if (canSwitch && f.getCode() == KeyCode.DOWN){
                        canSwitch = false;
                        gameStage.setScene(gameScene[2]);
                        gameRoot[2].requestFocus();
                        PauseTransition cooldown = new PauseTransition(Duration.millis(300)); 
                        cooldown.setOnFinished(ev -> canSwitch = true);
                        cooldown.play();
                    }   
                });
            }
            Rectangle[][][][][] squareContainer = new Rectangle[5][5][5][5][5];
            Tile[][][][][] tileArray = new Tile[5][5][5][5][5];
            Rectangle[][][][][] coverBox = new Rectangle[5][5][5][5][5];
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
                                coverBox[i][j][k][l][m] = new Rectangle(x,y,squareWidth,squareWidth);
                                tileArray[i][j][k][l][m] = new Tile(0, squareContainer[i][j][k][l][m], coverBox[i][j][k][l][m]);
                                coverBox[i][j][k][l][m].setFill(Color.GRAY);
                                squareContainer[i][j][k][l][m].setFill(Color.WHITE);
                                squareContainer[i][j][k][l][m].setStroke(Color.BLACK);
                                gameRoot[i].getChildren().add(squareContainer[i][j][k][l][m]);
                                gameRoot[i].getChildren().add(tileArray[i][j][k][l][m].getNumberLabel());
                                tileArray[i][j][k][l][m].getNumberLabel().setLayoutX(squareContainer[i][j][k][l][m].getX() + squareWidth / 4+1);
                                tileArray[i][j][k][l][m].getNumberLabel().setLayoutY(squareContainer[i][j][k][l][m].getY() + squareWidth / 4-3);
                                gameRoot[i].getChildren().add(coverBox[i][j][k][l][m]);
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
                onward = true;
                for (int i = 0; i<bombCount; i++){
                    bomb1[i] = random.nextInt(boardSize);
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
            for (int i = 0; i<boardSize; i++){
                for (int j = 0; j<boardSize; j++){
                    for (int k = 0; k<boardSize; k++){
                        for (int l = 0; l<boardSize; l++){
                            for (int m = 0; m<boardSize; m++){
                                for (int n = 0; n<bombCount; n++){
                                    if ((squareContainer[i][j][k][l][m] == squareContainer[bomb1[n]][bomb2[n]][bomb3[n]][bomb4[n]][bomb5[n]])){
                                        squareContainer[i][j][k][l][m].setFill(Color.BLACK);
                                        for (int o = -1; o <= 1; o++) {
                                            for (int p = -1; p <= 1; p++) {
                                                for (int q = -1; q <= 1; q++) {
                                                    for (int r = -1; r <= 1; r++) {
                                                        for (int s = -1; s <= 1; s++) {
                                                            int ni = i + o;
                                                            int nj = j + p;
                                                            int nk = k + q;
                                                            int nl = l + r;
                                                            int nm = m + s;
                                                            if (ni >= 0 && ni < boardSize &&
                                                                nj >= 0 && nj < boardSize &&
                                                                nk >= 0 && nk < boardSize &&
                                                                nl >= 0 && nl < boardSize &&
                                                                nm >= 0 && nm < boardSize) {
                                                                
                                                                // Don't update the bomb tile itself
                                                                if (!(o == 0 && p == 0 && q == 0 && r == 0 && s == 0)) {
                                                                    tileArray[ni][nj][nk][nl][nm].addBomb();
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }                                        
                                        tileArray[i][j][k][l][m].isBomb();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // This will track visited tiles during flood fill to prevent infinite loops
            HashSet<String> cleared = new HashSet<>();
            
            for (int i = 0; i<boardSize; i++){
                for (int j = 0; j<boardSize; j++){
                    for (int k = 0; k<boardSize; k++){
                        for (int l = 0; l<boardSize; l++){
                            for (int m = 0; m<boardSize; m++){
                                // Store the coordinates for use in the lambda
                                final int fi = i;
                                final int fj = j;
                                final int fk = k;
                                final int fl = l;
                                final int fm = m;
                                
                                tileArray[i][j][k][l][m].getCoverBox().setOnMouseClicked(f->{
                                    for (int ia = 0; ia<boardSize; ia++){
                                        for (int ja = 0; ja<boardSize; ja++){
                                            for (int ka = 0; ka<boardSize; ka++){
                                                for (int la = 0; la<boardSize; la++){
                                                    for (int ma = 0; ma<boardSize; ma++){
                                                        if (tileArray[ia][ja][ka][la][ma].getCoverBox().getFill() != Color.TRANSPARENT){
                                                            if (tileArray[ia][ja][ka][la][ma].getCoverBox().getFill() == Color.LIGHTBLUE || tileArray[ia][ja][ka][la][ma].getCoverBox().getFill() == Color.GRAY){
                                                                tileArray[ia][ja][ka][la][ma].getCoverBox().setFill(Color.GRAY);
                                                            }else {//"0xffa500ff"
                                                                tileArray[ia][ja][ka][la][ma].getCoverBox().setFill(Color.ORANGE);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (f.getButton() == MouseButton.PRIMARY){
                                        if (((Rectangle)f.getSource()).getFill() == Color.GRAY || ((Rectangle)f.getSource()).getFill() == Color.LIGHTBLUE){
                                            ((Rectangle)f.getSource()).setFill(Color.TRANSPARENT);
                                            
                                            // Clear HashSet for new flood fill operation
                                            cleared.clear();
                                            
                                            // Check if we hit a bomb
                                            for (int ia = 0; ia<boardSize; ia++){
                                                for (int ja = 0; ja<boardSize; ja++){
                                                    for (int ka = 0; ka<boardSize; ka++){
                                                        for (int la = 0; la<boardSize; la++){
                                                            for (int ma = 0; ma<boardSize; ma++){
                                                                if (((Rectangle)tileArray[ia][ja][ka][la][ma].getSquare()).getFill() == Color.BLACK && 
                                                                    ((Rectangle)tileArray[ia][ja][ka][la][ma].getCoverBox()).getFill() == Color.TRANSPARENT){
                                                                    Stage winStage = new Stage();
                                                                    
                                                                    
                                                                    VBox winRoot = new VBox(10);
                                                                    winRoot.setAlignment(Pos.CENTER);
                                                                    gameStage.close();
                                                                    Label winMessage = new Label("You Lose!");
                                                                    winMessage.setStyle("-fx-font-size: 20px;");

                                                                    Button closeButton = new Button("Return to Menu");
                                                                    closeButton.setOnAction(g -> {
                                                                        winStage.close();
                                                                        flagged = 0;
                                                                        primaryStage.show();
                                                                    });
                                                                    
                                                                    winRoot.getChildren().addAll(winMessage, closeButton);
                                                                    winRoot.setStyle("-fx-padding: 20px;");
                                                                    
                                                                    Scene winScene = new Scene(winRoot, 300, 150);
                                                                    winStage.setScene(winScene);
                                                                    winStage.show();
                                                                    return;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            
                                            // Check for win condition
                                            checkWinCondition(tileArray, boardSize, bombCount, gameStage, primaryStage);
                                            
                                            // If we clicked on a tile with 0 bombs, start flood fill
                                            if (tileArray[fi][fj][fk][fl][fm].getBombs() == 0) {
                                                floodFill(tileArray, fi, fj, fk, fl, fm, boardSize, cleared);
                                            }
                                        }
                                    }else if(f.getButton() == MouseButton.MIDDLE){
                                        Rectangle center = ((Rectangle)f.getSource());
                                        int centeri = -2;
                                        int centerj = -2;
                                        int centerk = -2;
                                        int centerl = -2;
                                        int centerm = -2;
                                        for (int ia = 0; ia<boardSize; ia++){
                                            for (int ja = 0; ja<boardSize; ja++){
                                                for (int ka = 0; ka<boardSize; ka++){
                                                    for (int la = 0; la<boardSize; la++){
                                                        for (int ma = 0; ma<boardSize; ma++){
                                                            if (center == tileArray[ia][ja][ka][la][ma].getCoverBox()){
                                                                if (tileArray[ia][ja][ka][la][ma].getCoverBox().getFill() != Color.TRANSPARENT){
                                                                    if (tileArray[ia][ja][ka][la][ma].getCoverBox().getFill() == Color.GRAY){
                                                                        tileArray[ia][ja][ka][la][ma].getCoverBox().setFill(Color.LIGHTBLUE);
                                                                    }else if (tileArray[ia][ja][ka][la][ma].getCoverBox().getFill() == Color.ORANGE){
                                                                        tileArray[ia][ja][ka][la][ma].getCoverBox().setFill(Color.rgb(202,196,129));
                                                                    }
                                                                }
                                                                centeri = ia;
                                                                centerj = ja;
                                                                centerk = ka;
                                                                centerl = la;
                                                                centerm = ma;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        for (int o = -1; o <= 1; o++) {
                                            for (int p = -1; p <= 1; p++) {
                                                for (int q = -1; q <= 1; q++) {
                                                    for (int r = -1; r <= 1; r++) {
                                                        for (int s = -1; s <= 1; s++) {
                                                            int ni = centeri + o;
                                                            int nj = centerj + p;
                                                            int nk = centerk + q;
                                                            int nl = centerl + r;
                                                            int nm = centerm + s;
                                                            if (ni < 0 || ni >= boardSize || nj < 0 || nj >= boardSize || nk < 0 || nk >= boardSize ||
                                                            nl < 0 || nl >= boardSize || nm < 0 || nm >= boardSize) {
                                                                continue;
                                                            }
                                                            // Check if the neighboring tile is within bounds and not yet revealed
                                                            if (ni >= 0 && ni < boardSize && nj >= 0 && nj < boardSize && 
                                                                nk >= 0 && nk < boardSize && nl >= 0 && nl < boardSize && 
                                                                nm >= 0 && nm < boardSize && 
                                                                tileArray[ni][nj][nk][nl][nm].getCoverBox().getFill() == Color.GRAY) {
                                                                    tileArray[ni][nj][nk][nl][nm].getCoverBox().setFill(Color.LIGHTBLUE);
                                                                
                                                            }
                                                            if (ni >= 0 && ni < boardSize && nj >= 0 && nj < boardSize && 
                                                                nk >= 0 && nk < boardSize && nl >= 0 && nl < boardSize && 
                                                                nm >= 0 && nm < boardSize && 
                                                                tileArray[ni][nj][nk][nl][nm].getCoverBox().getFill() == Color.ORANGE) {
                                                                    tileArray[ni][nj][nk][nl][nm].getCoverBox().setFill(Color.rgb(202,196,129));
                                                                
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        if (((Rectangle)f.getSource()).getFill() == Color.GRAY || ((Rectangle)f.getSource()).getFill() == Color.LIGHTBLUE){
                                            ((Rectangle)f.getSource()).setFill(Color.ORANGE);
                                            flagged++;
                                            for (int in = 0; in<boardSize; in++)
                                                bombLabel[in].setText("Unflagged Bombs: " + (bombCount-flagged)+"/"+(bombCount));
                                        }
                                        else if (((Rectangle)f.getSource()).getFill() == Color.ORANGE){
                                            ((Rectangle)f.getSource()).setFill(Color.GRAY);
                                            flagged--;
                                            for (int in = 0; in<boardSize; in++)
                                                bombLabel[in].setText("Unflagged Bombs: " + (bombCount-flagged)+"/"+(bombCount));
                                        }
                                        // Check for win condition after flagging a tile
                                        checkWinCondition(tileArray, boardSize, bombCount, gameStage, primaryStage);
                                    }
                                });
                                
                                // Display numbers
                                if (tileArray[i][j][k][l][m].getBombs() >= 1) {
                                    tileArray[i][j][k][l][m].setNumber(tileArray[i][j][k][l][m].getBombs());
                                    tileArray[i][j][k][l][m].showNumber(); // Display the number on the tile
                                }
                            }
                        }
                    }
                }
            }
        });
    }
    
    // Check if the player has won
    private void checkWinCondition(Tile[][][][][] tileArray, int boardSize, int bombCount, Stage gameStage, Stage primaryStage) {
        boolean allNonBombsRevealed = true;
        boolean allBombsFlagged = true;
        int flaggedBombs = 0;
        
        // Count how many non-bomb tiles are still covered and how many bombs are correctly flagged
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                for (int k = 0; k < boardSize; k++) {
                    for (int l = 0; l < boardSize; l++) {
                        for (int m = 0; m < boardSize; m++) {
                            Rectangle coverBox = tileArray[i][j][k][l][m].getCoverBox();
                            boolean isBomb = tileArray[i][j][k][l][m].getBombs() == -1;
                            boolean isCovered = coverBox.getFill() == Color.GRAY || coverBox.getFill() == Color.LIGHTBLUE;
                            boolean isFlagged = coverBox.getFill() == Color.ORANGE || coverBox.getFill() == Color.rgb(202,196,129);
                            
                            // If a non-bomb tile is still covered, player hasn't won yet
                            if (!isBomb && isCovered) {
                                allNonBombsRevealed = false;
                            }
                            
                            // Count bombs and correctly flagged bombs
                            if (isBomb) {
                                if (isFlagged) {
                                    flaggedBombs++;
                                } else {
                                    allBombsFlagged = false;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Win if all non-bomb tiles are revealed OR all bombs are correctly flagged
        if (allNonBombsRevealed || (allBombsFlagged && flaggedBombs == bombCount && allNonBombsRevealed)) {
            // Show win message
            Stage winStage = new Stage();
            
            
            VBox winRoot = new VBox(10);
            winRoot.setAlignment(Pos.CENTER);
            
            Label winMessage = new Label("Congratulations! You Win!");
            winMessage.setStyle("-fx-font-size: 20px;");
            gameStage.close();
            Button closeButton = new Button("Return to Menu");
            closeButton.setOnAction(e -> {
                winStage.close();
                flagged = 0;
                
                primaryStage.show();
            });
            
            winRoot.getChildren().addAll(winMessage, closeButton);
            winRoot.setStyle("-fx-padding: 20px;");
            
            Scene winScene = new Scene(winRoot, 300, 150);
            winStage.setScene(winScene);
            winStage.show();
        }
    }
    
    // Recursive flood fill function to clear empty spaces
    private void floodFill(Tile[][][][][] tileArray, int i, int j, int k, int l, int m, int boardSize, HashSet<String> cleared) {
        // Create a key for this position
        String key = i + "," + j + "," + k + "," + l + "," + m;
        
        // Check if we've already processed this position
        if (cleared.contains(key)) {
            return;
        }
        
        // Mark this position as cleared
        cleared.add(key);
        
        // Make sure we're within bounds
        if (i < 0 || i >= boardSize || j < 0 || j >= boardSize || k < 0 || k >= boardSize ||
            l < 0 || l >= boardSize || m < 0 || m >= boardSize) {
            return;
        }
        
        // Reveal this tile
        tileArray[i][j][k][l][m].getCoverBox().setFill(Color.TRANSPARENT);
        
        // If this tile has bombs around it, don't continue flood fill from here
        if (tileArray[i][j][k][l][m].getBombs() > 0) {
            return;
        }
        
        // If this tile is a bomb, don't continue flood fill (shouldn't happen because of game over)
        if (tileArray[i][j][k][l][m].getBombs() == -1) {
            return;
        }
        
        // Recursively flood fill all adjacent tiles (including diagonals in 5D)
        for (int o = -1; o <= 1; o++) {
            for (int p = -1; p <= 1; p++) {
                for (int q = -1; q <= 1; q++) {
                    for (int r = -1; r <= 1; r++) {
                        for (int s = -1; s <= 1; s++) {
                            // Skip the current tile
                            if (o == 0 && p == 0 && q == 0 && r == 0 && s == 0) {
                                continue;
                            }
                            
                            int ni = i + o;
                            int nj = j + p;
                            int nk = k + q;
                            int nl = l + r;
                            int nm = m + s;
                            
                            // Check if the neighboring tile is within bounds and not yet revealed
                            if (ni >= 0 && ni < boardSize && nj >= 0 && nj < boardSize && 
                                nk >= 0 && nk < boardSize && nl >= 0 && nl < boardSize && 
                                nm >= 0 && nm < boardSize && 
                                (tileArray[ni][nj][nk][nl][nm].getCoverBox().getFill() == Color.GRAY || tileArray[ni][nj][nk][nl][nm].getCoverBox().getFill() == Color.LIGHTBLUE)) {
                                
                                floodFill(tileArray, ni, nj, nk, nl, nm, boardSize, cleared);
                            }
                        }
                    }
                }
            }
        }
    }
}

class Tile {
    int bombs = -2; // -2 for uninitialized, -1 for a bomb, >= 0 for adjacent bomb count
    Rectangle square;
    Label numberLabel;  // This will display the number of adjacent bombs
    Rectangle coverBox;

    Tile(int bombs, Rectangle square, Rectangle coverBox) {
        this.bombs = bombs;
        this.square = square;
        this.coverBox = coverBox;
        this.numberLabel = new Label(""); // Initialize with no label
        this.numberLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;"); // Style the number
    }

    public int getBombs() {
        return this.bombs;
    }

    public Rectangle getSquare() {
        return this.square;
    }
    
    public Rectangle getCoverBox(){
        return this.coverBox;
    }

    public void addBomb() {
        this.bombs++;
    }

    public void isBomb() {
        this.bombs = -1; // Mark this tile as a bomb
    }

    public void setNumber(int number) {
        this.bombs = number; // Set the bomb count for adjacent bombs
    }

    public void showNumber() {
        if (bombs >= 0) {
            numberLabel.setText(String.valueOf(bombs));
        }
    }

    public Label getNumberLabel() {
        return this.numberLabel;
    }
}
