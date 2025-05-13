# 5D Minesweeper

## Synopsis
A 5 dimensional minesweeper game. Use arrow keys to move up and down layers! Finish your minesweeper mindgames.

## Motivation
It's a fun concept I've always wanted to use.

## How to Run
Just run the java file in your terminal or other code runner

## Code Example
Yes, this code includes many for-loops. Here's one: 
```
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
```
