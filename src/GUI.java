import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;


public class GUI extends Application {
    char side;
    boolean queueBlackOrWhite;
    boolean haveSource;
    int counterQueue;
    int counterGames;
    int counterWins;
    int counterLose;
    Coordinates source;
    Coordinates destiny;
    String login = "";
    Board board = new Board();
    ArrayList<Board> list = new ArrayList<>();
    ArrayList<String> listString = new ArrayList<>();
    public static final int TITLE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;
    GridPane gridPane = new GridPane();
    StackPane[][] stackPanes = new StackPane[8][8];
    Rectangle[][] rectangles = new Rectangle[8][8];
    Stage window;
    MenuBar menuBar = new MenuBar();
    Menu menuGame = new Menu("Gra");
    MenuItem menuItemNewGame = new MenuItem("Nowa Gra ctrl+N");
    MenuItem menuItemUndo = new MenuItem("Cofnij ruch ctrl+U");
    MenuItem menuItemSaveGame = new MenuItem("Zapisz grę ctrl+S");
    MenuItem menuItemLoadGame = new MenuItem("Wczytaj grę ctrl+L");
    RadioMenuItem radioMenuItemMute = new RadioMenuItem("Wycisz dźwięki");
    MenuItem menuItemStats = new MenuItem("Statyski");
    AudioClip check = new AudioClip(this.getClass().getResource("file/hahaha.mp3").toString());
    AudioClip beating = new AudioClip(this.getClass().getResource("file/vista.mp3").toString());
    AudioClip pip = new AudioClip(this.getClass().getResource("file/pip.mp3").toString());
    TextArea textArea = new TextArea();


    //tworzenie głównego okna aplikacji
    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.getIcons().add(new Image("file/BlackKing.png"));
        BackgroundSize backgroundSize = new BackgroundSize(900, 900, false, false, false, false);
        BackgroundImage backgroundImage = new BackgroundImage(new Image("file/tlo.png"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
        Background background = new Background(backgroundImage);
        gridPane.setBackground(background);
        //pomocnicze obiektu organizujące układ aplikacji
        Rectangle rectangleLeft = new Rectangle(50, 50);
        rectangleLeft.setVisible(false);
        GridPane.setConstraints(rectangleLeft, 0, 0);
        gridPane.getChildren().add(rectangleLeft);
        Rectangle rectangleRight = new Rectangle(50, 50);
        rectangleRight.setVisible(false);
        GridPane.setConstraints(rectangleRight, 9, 0);
        gridPane.getChildren().add(rectangleRight);
        Rectangle rectangleDown = new Rectangle(50, 50);
        rectangleDown.setVisible(false);
        GridPane.setConstraints(rectangleDown, 0, 9);
        gridPane.getChildren().add(rectangleDown);

        Scene scene = new Scene(gridPane, TITLE_SIZE * WIDTH + 500, TITLE_SIZE * HEIGHT + 100);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                rectangles[i][j] = new Rectangle(TITLE_SIZE, TITLE_SIZE);
                rectangles[i][j].setFill((i + j) % 2 == 0 ? Color.WHITE : Color.GRAY);
                stackPanes[i][j] = new StackPane();
                stackPanes[i][j].getChildren().add(rectangles[i][j]);
                GridPane.setConstraints(stackPanes[i][j], i + 1, 7 - j + 1);
                gridPane.getChildren().add(stackPanes[i][j]);
                stackPanes[i][j].setOnMouseClicked(e -> {
                    if (!queueBlackOrWhite) {
                        whiteMove(e);
                    } else {
                        blackMove(e);
                    }
                });
            }
        }
        //pole zapisu przebiegu partii
        GridPane.setConstraints(textArea, 10, 0, 1, 10);
        gridPane.getChildren().add(textArea);
        textArea.setFont(Font.font(30));
        textArea.setEditable(false);
        draw();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess");
        primaryStage.show();
        KeyCombination keyCombinationUndo = new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_ANY);
        KeyCombination keyCombinationNewGame = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_ANY);
        KeyCombination keyCombinationSave = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_ANY);
        KeyCombination keyCombinationLoad = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_ANY);
        Runnable runnableUndo = this::undo;
        Runnable runnableNew = this::menu;
        Runnable runnableSave = this::save;
        Runnable runnableLoad = this::load;
        scene.getAccelerators().put(keyCombinationUndo, runnableUndo);
        scene.getAccelerators().put(keyCombinationNewGame, runnableNew);
        scene.getAccelerators().put(keyCombinationSave, runnableSave);
        scene.getAccelerators().put(keyCombinationLoad, runnableLoad);
        menuBar.getMenus().add(menuGame);
        menuBar.setMinHeight(25);
        menuBar.setMaxHeight(25);
        menuItemNewGame.setOnAction(event -> menu());
        menuItemUndo.setOnAction(event -> undo());
        menuItemLoadGame.setOnAction(event -> load());
        menuItemSaveGame.setOnAction(event -> save());
        menuItemStats.setOnAction(event -> stats());
        menuGame.getItems().addAll(menuItemNewGame, menuItemUndo, menuItemLoadGame, menuItemSaveGame, radioMenuItemMute, menuItemStats);
        gridPane.getChildren().add(menuBar);
        menu();
    }

    private void undo() {
        if (list.size() > 1) {
            board = new Board(list.get(list.size() - 2).chessmen);
            textArea.setText(listString.get(listString.size() - 2));
            listString.remove(listString.size() - 1);
            list.remove(list.size() - 1);
            if (side == 'f') queueBlackOrWhite = !queueBlackOrWhite;
            if (!queueBlackOrWhite || side == 'b') counterQueue--;
            draw();
        }
    }

    //uzupełnianie pól szachownicy obrazami figór
    private void draw() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int k = board.getIndex(new Coordinates(i, j));
                if (stackPanes[i][j].getChildren().size() == 2) stackPanes[i][j].getChildren().remove(1);
                if (k != -1) {
                    stackPanes[i][j].getChildren().add(new ImageView(board.getChessman(k).getImage()));
                } else {
                    stackPanes[i][j].getChildren().add(new ImageView("file/Empty.png"));
                }
            }
        }
    }

    //metoda do obliczania które pole na szachownicy zostało wybrane
    private Coordinates calculateCoordinates(double x, double y) {
        int xResult;
        int yResult;
        xResult = (int) x;
        xResult -= 50;
        xResult = xResult / 100;
        yResult = 850 - (int) y;
        yResult = yResult / 100;
        return new Coordinates(xResult, yResult);
    }

    //obsługa ruchu białych figur sterowanych przez gracza
    private void whiteMove(MouseEvent e) {
        if (haveSource) {
            //obliczanie pola docelowego
            destiny = calculateCoordinates(e.getSceneX(), e.getSceneY());
            //sprawdza czy zródło ma figure i czy pole docelowe jest w jej zakresie
            if (board.getChessman(source) != null && board.getChessman(source).possibleMoves(board).contains(destiny)) {
                //jeśli na polu docelowym znajdue się figura przeciwnka pozbywa się jej
                if (board.getChessman(destiny) != null) {
                    playMusic('b');
                    board.deleteChessman(destiny);
                }
                //jesli pion promuje nastepuje jego promocja
                if (board.getChessman(source).getSymbol() == 'P' && destiny.getY() == 7) {
                    board.deleteChessman(source);
                    PromotionWindow('w', source, destiny);
                    //w przeciwnym wypadku obsługa dalszego ruchu
                } else {
                    //obsługa roszady
                    CastlingWhite(source, destiny);
                    //wykonanie ruchu
                    board.getChessman(source).setCoordinates(destiny);
                    textArea.appendText(move(source, destiny, board.getChessman(destiny).getSymbol(), 'x'));
                    draw();
                    if (board.areMovesLeft('b')) {
                        if (board.isCheck('b')) {
                            playMusic('h');
                        }
                        if (side == 'w') {
                            computerMove('b');
                        } else {
                            queueBlackOrWhite = true;
                        }
                        draw();
                    } else {
                        if (board.isCheck('b')) {
                            menuEndGame('w');
                        } else {
                            menuEndGame('p');
                        }
                    }
                    list.add(new Board(board.chessmen));
                    listString.add(textArea.getText());
                }
            }
            playMusic('p');
            haveSource = false;
            turnOffLight();
        } else {
            Coordinates x = calculateCoordinates(e.getSceneX(), e.getSceneY());
            if (board.getChessman(x) != null) {
                if (board.getChessman(x).getColor() == 'w') {
                    getSource(e);
                }
            }
            playMusic('p');
        }
    }

    //obsługa ruchu czarnych figur sterowanych przez gracza
    private void blackMove(MouseEvent e) {
        if (haveSource) {
            destiny = calculateCoordinates(e.getSceneX(), e.getSceneY());
            if (board.getChessman(source) != null &&
                    board.getChessman(source).possibleMoves(board).contains(destiny)) {
                if (board.getChessman(destiny) != null) {
                    playMusic('b');
                    board.deleteChessman(destiny);
                }
                if (board.getChessman(source).getSymbol() == 'p' && destiny.getY() == 0) {
                    board.deleteChessman(source);
                    PromotionWindow('b', source, destiny);
                } else {
                    CastlingBlack(source, destiny);
                    board.getChessman(source).setCoordinates(destiny);
                    textArea.appendText(move(source, destiny, board.getChessman(destiny).getSymbol(), 'x'));
                    draw();
                    if (board.areMovesLeft('w')) {
                        if (board.isCheck('w')) {
                            playMusic('h');
                        }
                        if (side == 'b') {
                            computerMove('w');
                        } else {
                            queueBlackOrWhite = false;
                        }
                        draw();
                    } else {
                        if (board.isCheck('w')) {
                            menuEndGame('b');
                        } else {
                            menuEndGame('p');
                        }
                    }
                }
                list.add(new Board(board.chessmen));
                listString.add(textArea.getText());
            }
            playMusic('p');
            haveSource = false;
            turnOffLight();
        } else {
            Coordinates x = calculateCoordinates(e.getSceneX(), e.getSceneY());
            if (board.getChessman(x) != null) {
                if (board.getChessman(x).getColor() == 'b') {
                    getSource(e);
                }
            }
            playMusic('p');
        }
    }

    //obsługa ruchów komputera; color=b/w
    private void computerMove(char color) {
        ArrayList<Chessman> computerAvailableChessman = new ArrayList<>();
        for (Chessman temp : board.chessmen) {
            if (temp.getColor() == color && temp.possibleMoves(board).size() != 0) {
                computerAvailableChessman.add(temp);
            }
        }
        Random random = new Random();
        int chosenChessman = random.nextInt(computerAvailableChessman.size());
        int chosenMove = random.nextInt(computerAvailableChessman.get(chosenChessman).possibleMoves(board).size());
        Coordinates source = new Coordinates(computerAvailableChessman.get(chosenChessman).getCoordinates());
        Coordinates destiny = new Coordinates(computerAvailableChessman.get(chosenChessman).possibleMoves(board).get(chosenMove));
        if (color == 'w') {
            if (board.getChessman(destiny) != null) {
                playMusic('b');
                board.deleteChessman(destiny);
            }
            if (board.getChessman(source).getSymbol() == 'p' && destiny.getY() == 0) {
                board.addChessman('w', 'H', destiny);
                textArea.appendText(move(source, destiny, 'P', 'H'));
                board.deleteChessman(source);
            } else {
                CastlingWhite(source, destiny);
                board.getChessman(source).setCoordinates(destiny);
                textArea.appendText(move(source, destiny, board.getChessman(destiny).getSymbol(), 'x'));
                if (board.areMovesLeft('b')) {
                    if (board.isCheck('b')) {
                        playMusic('h');
                    }
                } else {
                    if (board.isCheck('b')) {
                        menuEndGame('w');
                    } else {
                        menuEndGame('p');
                    }
                }
            }
        } else {
            if (board.getChessman(destiny) != null) board.deleteChessman(destiny);
            if (board.getChessman(source).getSymbol() == 'p' && destiny.getY() == 0) {
                board.addChessman('b', 'h', destiny);
                textArea.appendText(move(source, destiny, 'p', 'h'));
                board.deleteChessman(source);
            } else {
                CastlingBlack(source, destiny);
                board.getChessman(source).setCoordinates(destiny);
                textArea.appendText(move(source, destiny, board.getChessman(destiny).getSymbol(), 'x'));
                if (board.areMovesLeft('w')) {
                    if (board.isCheck('w')) {
                        playMusic('h');
                    }
                } else {
                    if (board.isCheck('w')) {
                        menuEndGame('b');
                    } else {
                        menuEndGame('p');
                    }
                }
            }
        }
    }

    //wybieranie figury która będzie się poruszać
    private void getSource(MouseEvent e) {
        turnOffLight();
        source = calculateCoordinates(e.getSceneX(), e.getSceneY());
        turnOnLight(board.getChessman(source).possibleMoves(board));
        if (board.getChessman(source).possibleMoves(board).size() != 0) {
            haveSource = true;
        } else {
            turnOnLight(source);
        }
    }

    //podświetlenie możliwych ruchów
    private void turnOnLight(ArrayList<Coordinates> possibleMoves) {
        for (Coordinates possibleMove : possibleMoves) {
            int x = possibleMove.getX();
            int y = possibleMove.getY();
            rectangles[x][y].setFill((x + y) % 2 == 0 ? Color.YELLOW : Color.GREEN);
        }
    }

    //podświetenie wybranego źródła, jeśli nie posiada ono możliwych ruchów
    private void turnOnLight(Coordinates source) {
        int x = source.getX();
        int y = source.getY();
        rectangles[x][y].setFill((x + y) % 2 == 0 ? Color.YELLOW : Color.GREEN);
    }

    //wyłączenie podświetlenia pól
    void turnOffLight() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                rectangles[i][j].setFill((i + j) % 2 == 0 ? Color.WHITE : Color.GRAY);
            }
        }
    }

    //obsługa przemieszczania się wieży dla roszady pionków białych
    private void CastlingWhite(Coordinates source, Coordinates destiny) {
        if (board.getChessman(source).getSymbol() == 'K' && !board.getChessman(source).isMove() && destiny.equals(new Coordinates(2, 0))) {
            board.getChessman(new Coordinates(0, 0)).setCoordinates(new Coordinates(3, 0));
        }
        if (board.getChessman(source).getSymbol() == 'K' && !board.getChessman(source).isMove() && destiny.equals(new Coordinates(6, 0))) {
            board.getChessman(new Coordinates(7, 0)).setCoordinates(new Coordinates(5, 0));
        }
    }

    //obsługa przemieszczania się wieży dla roszady pionków czarnych
    private void CastlingBlack(Coordinates source, Coordinates destiny) {
        if (board.getChessman(source).getSymbol() == 'k' && !board.getChessman(source).isMove() && destiny.equals(new Coordinates(2, 7))) {
            board.getChessman(new Coordinates(0, 7)).setCoordinates(new Coordinates(3, 7));
        }
        if (board.getChessman(source).getSymbol() == 'k' && !board.getChessman(source).isMove() && destiny.equals(new Coordinates(6, 7))) {
            board.getChessman(new Coordinates(7, 7)).setCoordinates(new Coordinates(5, 7));
        }
    }

    //okno promocji piona z wyborem figury promocyjnej
    void PromotionWindow(char color, Coordinates source, Coordinates destiny) {
        GridPane gridPane1 = new GridPane();
        StackPane[] stackPanes1 = new StackPane[4];
        for (int i = 0; i < stackPanes1.length; i++) {
            stackPanes1[i] = new StackPane();
            stackPanes1[i].setMinSize(100, 100);
            GridPane.setConstraints(stackPanes1[i], i, 0);
            gridPane1.getChildren().add(stackPanes1[i]);
        }
        if (color == 'w') {
            stackPanes1[0].getChildren().add(new ImageView("file/WhiteQueen.png"));
            stackPanes1[1].getChildren().add(new ImageView("file/WhiteRook.png"));
            stackPanes1[2].getChildren().add(new ImageView("file/WhiteBishop.png"));
            stackPanes1[3].getChildren().add(new ImageView("file/WhiteKnight.png"));
        } else {
            stackPanes1[0].getChildren().add(new ImageView("file/BlackQueen.png"));
            stackPanes1[1].getChildren().add(new ImageView("file/BlackRook.png"));
            stackPanes1[2].getChildren().add(new ImageView("file/BlackBishop.png"));
            stackPanes1[3].getChildren().add(new ImageView("file/BlackKnight.png"));
        }
        Scene secondScene = new Scene(gridPane1, TITLE_SIZE * 4, TITLE_SIZE);
        Stage newWindow = new Stage();
        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.setOnCloseRequest(e -> {
            board.addChessman(color, 'h', destiny);
            board.getChessman(destiny).setMove();
            if (source.getY() == 6) {
                textArea.appendText(move(source, destiny, 'P', 'H'));
            } else {
                textArea.appendText(move(source, destiny, 'p', 'h'));
            }
            if (side == 'w') {
                computerMove('b');
            } else if (side == 'b') {
                computerMove('w');
            }
            draw();
        });
        newWindow.setTitle("Wybór figury");
        newWindow.setScene(secondScene);
        newWindow.show();
        queueBlackOrWhite = !queueBlackOrWhite;
        stackPanes1[0].setOnMouseClicked(e -> {
            board.addChessman(color, 'h', destiny);
            board.getChessman(destiny).setMove();
            if (source.getY() == 6) {
                textArea.appendText(move(source, destiny, 'P', 'H'));
            } else {
                textArea.appendText(move(source, destiny, 'p', 'h'));
            }
            if (side == 'w') {
                computerMove('b');
                queueBlackOrWhite = !queueBlackOrWhite;
            } else if (side == 'b') {
                computerMove('w');
                queueBlackOrWhite = !queueBlackOrWhite;
            }
            draw();
            newWindow.close();
        });
        stackPanes1[1].setOnMouseClicked(e -> {
            board.addChessman(color, 'w', destiny);
            board.getChessman(destiny).setMove();
            if (source.getY() == 6) {
                textArea.appendText(move(source, destiny, 'P', 'W'));
            } else {
                textArea.appendText(move(source, destiny, 'p', 'W'));
            }
            if (side == 'w') {
                computerMove('b');
                queueBlackOrWhite = !queueBlackOrWhite;
            } else if (side == 'b') {
                computerMove('w');
                queueBlackOrWhite = !queueBlackOrWhite;
            }
            draw();
            newWindow.close();
        });
        stackPanes1[2].setOnMouseClicked(e -> {
            board.addChessman(color, 'g', destiny);
            board.getChessman(destiny).setMove();
            if (source.getY() == 6) {
                textArea.appendText(move(source, destiny, 'P', 'G'));
            } else {
                textArea.appendText(move(source, destiny, 'p', 'g'));
            }
            if (side == 'w') {
                computerMove('b');
                queueBlackOrWhite = !queueBlackOrWhite;
            } else if (side == 'b') {
                computerMove('w');
                queueBlackOrWhite = !queueBlackOrWhite;
            }
            draw();
            newWindow.close();
        });
        stackPanes1[3].setOnMouseClicked(e -> {
            board.addChessman(color, 's', destiny);
            board.getChessman(destiny).setMove();
            if (source.getY() == 6) {
                textArea.appendText(move(source, destiny, 'P', 'S'));
            } else {
                textArea.appendText(move(source, destiny, 'p', 's'));
            }
            if (side == 'w') {
                computerMove('b');
                queueBlackOrWhite = !queueBlackOrWhite;
            } else if (side == 'b') {
                computerMove('w');
                queueBlackOrWhite = !queueBlackOrWhite;
            }
            draw();
            newWindow.close();
        });
    }

    //menu startowe; wybór trybu gry; opcjonalnie koloru
    void menu() {
        GridPane gridPane = new GridPane();
        side = 'b';
        queueBlackOrWhite = false;
        counterQueue = 0;
        textArea.clear();
        listString.clear();
        list.clear();
        menuItemStats.setVisible(false);
        StackPane[] stackPanes = new StackPane[2];
        for (int i = 0; i < stackPanes.length; i++) {
            stackPanes[i] = new StackPane();
            stackPanes[i].setMinSize(300, 300);
            GridPane.setConstraints(stackPanes[i], i, 0);
            gridPane.getChildren().add(stackPanes[i]);
        }
        stackPanes[0].getChildren().add(new ImageView("file/playerVsPlayer.png"));
        stackPanes[1].getChildren().add(new ImageView("file/playerVsComputer.png"));
        Scene scene = new Scene(gridPane, TITLE_SIZE * 6, TITLE_SIZE * 3);
        Stage newWindow = new Stage();
        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.setOnCloseRequest(e -> {
            side = 'f';
            board = new Board();
            draw();
        });
        newWindow.setTitle("Ustawienia");
        newWindow.setScene(scene);
        newWindow.show();
        stackPanes[0].setOnMouseClicked(e -> {
            if (side == 'b') {
                side = 'f';
            } else {
                side = 'w';
                login();
            }
            if (list.size() == 0) {
                list.add(board);
                listString.add(textArea.getText());
            }
            board = new Board();
            draw();
            newWindow.close();
        });
        stackPanes[1].setOnMouseClicked(e -> {
            if (side == 'b') {
                side = 'f';
                stackPanes[0].getChildren().remove(0);
                stackPanes[0].getChildren().add(new ImageView("file/White.png"));
                stackPanes[1].getChildren().remove(0);
                stackPanes[1].getChildren().add(new ImageView("file/Black.png"));
            } else {
                board = new Board();
                side = 'b';
                login();
                computerMove('w');
                queueBlackOrWhite = true;
                draw();
                if (list.size() != 0) list.remove(0);
                list.add(new Board(board.chessmen));
                listString.add(textArea.getText());
                newWindow.close();
            }
        });
    }

    //odtwarzanie plików dźwiękowych
    private void playMusic(char chooser) {
        if (!radioMenuItemMute.isSelected()) {
            switch (chooser) {
                case 'h':
                    check.play();
                    break;
                case 'b':
                    beating.play();
                case 'p':
                    pip.play(500);
            }
        }
    }

    //menu wyświetlające się na koniec partii
    private void menuEndGame(char winOrPat) {
        GridPane gridPane = new GridPane();
        Button button1 = new Button("New Game");
        Button button2 = new Button("Zapisz rozgrywkę");
        Button button3 = new Button("End Game");
        Label label = new Label();
        list.clear();
        if (winOrPat == 'b') {
            label.setText("Szach i Mat. Czarne wygrały");
        } else if (winOrPat == 'w') {
            label.setText("Szach i Mat. Białe wygrały");
        } else {
            label.setText("Brak możłiwośći ruchu. Pat");
        }
        if (side != 'f') {
            if (winOrPat == side) {
                counterWins++;
            } else if (winOrPat != 'p') {
                counterLose++;
            }
            counterGames++;
            saveStats();
        }
        GridPane.setConstraints(label, 0, 0, 3, 1);
        GridPane.setConstraints(button1, 0, 1);
        GridPane.setConstraints(button2, 1, 1);
        GridPane.setConstraints(button3, 2, 1);
        gridPane.setVgap(10);
        gridPane.setHgap(5);
        gridPane.getChildren().addAll(button1, button2, button3, label);
        Scene scene = new Scene(gridPane);
        Stage newWindow = new Stage();
        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.setOnCloseRequest(e -> {
            window.close();
        });
        button1.setOnMouseClicked(e -> {
            side = 'b';
            menu();
            newWindow.close();
        });
        button2.setOnMouseClicked(e -> {
            saveGame();
        });
        button3.setOnMouseClicked(e -> {
            newWindow.close();
            window.close();
        });
        newWindow.setTitle("Koniec");
        newWindow.setScene(scene);
        newWindow.show();
    }

    //zamiana litery oznaczającej figurę, na symbol figury do wyświetlenia w pliku tekstowym
    private char returnSymbol(char inputSymbol) {
        switch (inputSymbol) {
            case 'K':
                return '\u2654';
            case 'H':
                return '\u2655';
            case 'W':
                return '\u2656';
            case 'G':
                return '\u2657';
            case 'S':
                return '\u2658';
            case 'P':
                return '\u2659';
            case 'k':
                return '\u265A';
            case 'h':
                return '\u265B';
            case 'w':
                return '\u265C';
            case 'g':
                return '\u265D';
            case 's':
                return '\u265E';
            case 'p':
                return '\u265F';
        }
        return 'x';
    }

    //zwracany zapis ruchu w notacji szachowej
    private String move(Coordinates source, Coordinates destiny, char symbol, char symbolPromotion) {
        if (Character.isLowerCase(symbol)) {
            if (symbol == 'k' && source.getX() == 4 && destiny.getX() == 2) return "\tO-O-O\n";
            if (symbol == 'k' && source.getX() == 4 && destiny.getX() == 6) return "\tO-O\n";
            if (symbol == 'p' && destiny.getY() == 0)
                return "\t" + decodeCoordinates(source) + "-" + decodeCoordinates(destiny) + returnSymbol(symbolPromotion) + "\n";
            return " " + returnSymbol(symbol) + decodeCoordinates(source) + "-" + decodeCoordinates(destiny) + "\n";
        } else {
            counterQueue++;
            if (symbol == 'K' && source.getX() == 4 && destiny.getX() == 2)
                return String.valueOf(counterQueue) + "." + "O-O-O";
            if (symbol == 'K' && source.getX() == 4 && destiny.getX() == 6)
                return String.valueOf(counterQueue) + "." + "O-O";
            if (symbol == 'P' && destiny.getY() == 7)
                return String.valueOf(counterQueue) + "." + decodeCoordinates(source) + "-" + decodeCoordinates(destiny) + returnSymbol(symbolPromotion);
            return counterQueue + "." + returnSymbol(symbol) + decodeCoordinates(source) + "-" + decodeCoordinates(destiny);
        }
    }

    //zamiana współrzędnych x i y na notacje szachową
    private String decodeCoordinates(Coordinates coordinates) {
        String result = "";
        switch (coordinates.getX()) {
            case 0:
                result += 'a';
                break;
            case 1:
                result += 'b';
                break;
            case 2:
                result += 'c';
                break;
            case 3:
                result += 'd';
                break;
            case 4:
                result += 'e';
                break;
            case 5:
                result += 'f';
                break;
            case 6:
                result += 'g';
                break;
            case 7:
                result += 'h';
                break;
        }
        result += String.valueOf(coordinates.getY() + 1);
        return result;
    }

    //wczytywanie ostatnio zapisanej gry
    private void load() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("src/saveAndStats/lastGame.txt"));
            side = (char) objectInputStream.readObject();
            if (side != 'f') menuItemStats.setVisible(true);
            queueBlackOrWhite = (boolean) objectInputStream.readObject();
            haveSource = (boolean) objectInputStream.readObject();
            counterQueue = (int) objectInputStream.readObject();
            list = (ArrayList<Board>) objectInputStream.readObject();
            listString = (ArrayList<String>) objectInputStream.readObject();
            counterGames = (int) objectInputStream.readObject();
            counterWins = (int) objectInputStream.readObject();
            counterLose = (int) objectInputStream.readObject();
            login = (String) objectInputStream.readObject();
            board = list.get(list.size() - 1);
            textArea.setText(listString.get(listString.size() - 1));
            draw();
            turnOffLight();
        } catch (Exception ignored) {}
    }

    //zapisywanie stanu gry
    private void save() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("src/saveAndStats/lastGame.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(side);
            objectOutputStream.writeObject(queueBlackOrWhite);
            objectOutputStream.writeObject(haveSource);
            objectOutputStream.writeObject(counterQueue);
            objectOutputStream.writeObject(list);
            objectOutputStream.writeObject(listString);
            objectOutputStream.writeObject(counterGames);
            objectOutputStream.writeObject(counterWins);
            objectOutputStream.writeObject(counterLose);
            objectOutputStream.writeObject(login);
            fileOutputStream.close();
        } catch (Exception ignored) {}
    }

    //zapis przebiegu partii
    private void saveGame() {
        VBox vBox = new VBox(2);
        Button button1 = new Button("Zapisz");
        TextField textField = new TextField();
        textField.appendText("Wprowadź nazwę pliku");
        textField.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(textField, button1);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox, 150, 50);
        Stage newWindow = new Stage();
        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.setOnCloseRequest(e -> {
            try {
                PrintWriter printWriter = new PrintWriter("src/saveAndStats/" + "temp.txt");
                printWriter.print(textArea.getText());
                printWriter.close();
            } catch (Exception exception) {
                System.out.println(exception);
            }
        });
        button1.setOnMouseClicked(e -> {
            try {
                PrintWriter printWriter = new PrintWriter("src/saveAndStats/" + textField.getText() + ".txt");
                printWriter.print(textArea.getText());
                printWriter.close();
            } catch (Exception exception) {
                System.out.println(exception);
            }
            newWindow.close();
        });
        newWindow.setTitle("Zapis");
        newWindow.setScene(scene);
        newWindow.show();
    }


    //wprowadzanie loginu obecnego gracza
    private void login() {
        VBox vBox = new VBox(2);
        Button button1 = new Button("Zaloguj");
        TextField textField = new TextField();
        textField.appendText("Wprowadź login");
        textField.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(textField, button1);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox, 150, 50);
        Stage newWindow = new Stage();
        newWindow.initModality(Modality.APPLICATION_MODAL);
        button1.setOnMouseClicked(e -> {
            login = textField.getText();
            loadStats();
            newWindow.close();
        });
        menuItemStats.setVisible(true);
        newWindow.setTitle("Logowanie");
        newWindow.setScene(scene);
        newWindow.show();
    }

    //pokazywanie statystyk dla obecnego gracza
    private void stats() {
        VBox vBox = new VBox(4);
        Label namePlayer = new Label(login + ":");
        Label playedGames = new Label("Ilość rozegrnaych partii: " + counterGames);
        Label winGames = new Label("Ilość wygranyych partii: " + counterWins);
        Label percentWinGames;
        if (counterGames == 0) {
            percentWinGames = new Label("Procent wygranych: - %");
        } else {
            percentWinGames = new Label("Procent wygranych: " + ((counterWins / (double) counterGames) * 100) + "%");
        }
        Label loseGames = new Label("Ilość przegranych partii: " + counterLose);
        vBox.getChildren().addAll(namePlayer, playedGames, winGames, percentWinGames, loseGames);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox, 250, 100);
        Stage newWindow = new Stage();
        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.setTitle("Statystki");
        newWindow.setScene(scene);
        newWindow.show();
    }

    //wczytywanie statystyk dla gracza(jeśli istnieją)
    private void loadStats() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("src/saveAndStats/" + login + ".txt"));
            counterGames = (int) objectInputStream.readObject();
            counterWins = (int) objectInputStream.readObject();
            counterLose = (int) objectInputStream.readObject();
        } catch (Exception ignored) {
        }
    }

    //zapisywanie statystyk do pliku z nazwą aktualnego gracza
    private void saveStats() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("src/saveAndStats/" + login + ".txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(counterGames);
            objectOutputStream.writeObject(counterWins);
            objectOutputStream.writeObject(counterLose);
            fileOutputStream.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}