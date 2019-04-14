/*
 *    Copyright 2014 - 2017 Yannick Watier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/**
 * Created by yannick on 4/18/2017.
 */

const Game = {
    template:
        `
<div id="main-div">
    <nav id="navbar-main-menu" class="navbar navbar-expand-lg">
      <a class="navbar-brand" href="#">
        <img src="images/EcheChess.svg" width="40" height="60" class="d-inline-block align-top">
      </a>
      <a class="nav-link" v-on:click="newGame">New Game</a>
      <a class="nav-link">Join Game</a>
    </nav>
    <div id="game">
        <div id="board">
            <div class="bord-case" v-bind:data-case-id="key" v-for="(piece, key, index) in board">
                <span class="board-pieces" draggable="true" v-html="piece.unicodeIcon"></span>
            </div>
        </div>
        <div id="game-points">
            <span>Black: {{blackPlayerScore}}</span>
            <span>White: {{whitePlayerScore}}</span>
        </div>
        <button class="btn btn-outline-light" type="button" data-toggle="collapse" data-target="#collapseGameLog">
            Show logs
        </button>
        <div class="collapse" id="collapseGameLog">
          <div class="card card-body">
            <div class="form-control" v-for="(log, index) in eventLog">
                {{log}}<br/>
            </div>
          </div>
        </div>
    </div>
    <div class="modal" id="new-game-modal" tabindex="-1" role="dialog">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Create a new game</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                <form>
                    
                    <select v-model="gameSide">
                        <option>WHITE</option>
                        <option>BLACK</option>
                        <option>OBSERVER</option>
                    </select>
                
                    <div class="form-group">
                        <label for="new-game-special-game">Special game type</label>
                        <input type="text" v-model="specialGamePattern" class="form-control form-control-sm" id="new-game-special-game" aria-describedby="new-game-special-game-text" placeholder="Special Game Pattern">
                        <small id="new-game-special-game-text" class="form-text text-muted">A8:W_KING;B8:W_ROOK[...]</small>
                    </div>
                    <div class="form-check">
                        <input type="checkbox" v-model="againstComputer" class="form-check-input" id="new-game-against-computer">
                        <label class="form-check-label" for="new-game-against-computer">Play against computer</label>
                    </div>
                    <div class="form-check">
                        <input type="checkbox" v-model="observers" class="form-check-input" id="new-game-observer">
                        <label class="form-check-label" for="new-game-observer">Allows observers</label>
                    </div>
                </form>
                </div>
                <div class="modal-footer">
                    <button type="button" v-on:click="createNewGameWithProperties" class="btn btn-primary">Create game</button>
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
</div>
`,
    data: function () {
        return {
            stompClient: null,
            blackPlayerScore: 0,
            whitePlayerScore: 0,
            gameUuid: null,
            gameSide: "WHITE",
            againstComputer: false,
            observers: true,
            specialGamePattern: null,
            board: {
                H8: {
                    "unicodeIcon": "&#9820;",
                    "name": "Black Rook"
                },
                G8: {
                    "unicodeIcon": " &#9822;",
                    "name": "Black Knight"
                },
                F8: {
                    "unicodeIcon": "&#9821;",
                    "name": "Black Bishop"
                },
                E8: {
                    "unicodeIcon": "&#9818;",
                    "name": "Black King"
                },
                D8: {
                    "unicodeIcon": "&#9819;",
                    "name": "Black Queen "
                },
                C8: {
                    "unicodeIcon": "&#9821;",
                    "name": "Black Bishop"
                },
                B8: {
                    "unicodeIcon": " &#9822;",
                    "name": "Black Knight"
                },
                A8: {
                    "unicodeIcon": "&#9820;",
                    "name": "Black Rook"
                },
                H7: {
                    "unicodeIcon": "&#9823;",
                    "name": "Black Pawn"
                },
                G7: {
                    "unicodeIcon": "&#9823;",
                    "name": "Black Pawn"
                },
                F7: {
                    "unicodeIcon": "&#9823;",
                    "name": "Black Pawn"
                },
                E7: {
                    "unicodeIcon": "&#9823;",
                    "name": "Black Pawn"
                },
                D7: {
                    "unicodeIcon": "&#9823;",
                    "name": "Black Pawn"
                },
                C7: {
                    "unicodeIcon": "&#9823;",
                    "name": "Black Pawn"
                },
                B7: {
                    "unicodeIcon": "&#9823;",
                    "name": "Black Pawn"
                },
                A7: {
                    "unicodeIcon": "&#9823;",
                    "name": "Black Pawn"
                },
                H6: {
                    "unicodeIcon": "",
                    "name": ""
                },
                G6: {
                    "unicodeIcon": "",
                    "name": ""
                },
                F6: {
                    "unicodeIcon": "",
                    "name": ""
                },
                E6: {
                    "unicodeIcon": "",
                    "name": ""
                },
                D6: {
                    "unicodeIcon": "",
                    "name": ""
                },
                C6: {
                    "unicodeIcon": "",
                    "name": ""
                },
                B6: {
                    "unicodeIcon": "",
                    "name": ""
                },
                A6: {
                    "unicodeIcon": "",
                    "name": ""
                },
                H5: {
                    "unicodeIcon": "",
                    "name": ""
                },
                G5: {
                    "unicodeIcon": "",
                    "name": ""
                },
                F5: {
                    "unicodeIcon": "",
                    "name": ""
                },
                E5: {
                    "unicodeIcon": "",
                    "name": ""
                },
                D5: {
                    "unicodeIcon": "",
                    "name": ""
                },
                C5: {
                    "unicodeIcon": "",
                    "name": ""
                },
                B5: {
                    "unicodeIcon": "",
                    "name": ""
                },
                A5: {
                    "unicodeIcon": "",
                    "name": ""
                },
                H4: {
                    "unicodeIcon": "",
                    "name": ""
                },
                G4: {
                    "unicodeIcon": "",
                    "name": ""
                },
                F4: {
                    "unicodeIcon": "",
                    "name": ""
                },
                E4: {
                    "unicodeIcon": "",
                    "name": ""
                },
                D4: {
                    "unicodeIcon": "",
                    "name": ""
                },
                C4: {
                    "unicodeIcon": "",
                    "name": ""
                },
                B4: {
                    "unicodeIcon": "",
                    "name": ""
                },
                A4: {
                    "unicodeIcon": "",
                    "name": ""
                },
                H3: {
                    "unicodeIcon": "",
                    "name": ""
                },
                G3: {
                    "unicodeIcon": "",
                    "name": ""
                },
                F3: {
                    "unicodeIcon": "",
                    "name": ""
                },
                E3: {
                    "unicodeIcon": "",
                    "name": ""
                },
                D3: {
                    "unicodeIcon": "",
                    "name": ""
                },
                C3: {
                    "unicodeIcon": "",
                    "name": ""
                },
                B3: {
                    "unicodeIcon": "",
                    "name": ""
                },
                A3: {
                    "unicodeIcon": "",
                    "name": ""
                },
                H2: {
                    "unicodeIcon": "&#9817;",
                    "name": "White Pawn"
                },
                G2: {
                    "unicodeIcon": "&#9817;",
                    "name": "White Pawn"
                },
                F2: {
                    "unicodeIcon": "&#9817;",
                    "name": "White Pawn"
                },
                E2: {
                    "unicodeIcon": "&#9817;",
                    "name": "White Pawn"
                },
                D2: {
                    "unicodeIcon": "&#9817;",
                    "name": "White Pawn"
                },
                C2: {
                    "unicodeIcon": "&#9817;",
                    "name": "White Pawn"
                },
                B2: {
                    "unicodeIcon": "&#9817;",
                    "name": "White Pawn"
                },
                A2: {
                    "unicodeIcon": "&#9817;",
                    "name": "White Pawn"
                },
                H1: {
                    "unicodeIcon": "&#9814;",
                    "name": "White Rook"
                },
                G1: {
                    "unicodeIcon": "&#9816;",
                    "name": "White Knight"
                },
                F1: {
                    "unicodeIcon": "&#9815;",
                    "name": "White Bishop"
                },
                E1: {
                    "unicodeIcon": "&#9812;",
                    "name": "White King"
                },
                D1: {
                    "unicodeIcon": "&#9813;",
                    "name": "White Queen "
                },
                C1: {
                    "unicodeIcon": "&#9815;",
                    "name": "White Bishop"
                },
                B1: {
                    "unicodeIcon": "&#9816;",
                    "name": "White Knight"
                },
                A1: {
                    "unicodeIcon": "&#9814;",
                    "name": "White Rook"
                }
            },
            eventLog: []
        };
    },
    mounted: function () {
        this.registerEvents();
    },
    methods: {
        updateBoardPieces: function (items) {
            let length = items.length;
            for (let i = 0; i < length; i++) {
                let newPiece = items[i];
                let piece = this.board[newPiece.rawPosition];
                piece.unicodeIcon = newPiece.unicodeIcon;
                piece.name = newPiece.name;
            }
        },
        //---------------------------------------------------------------------------
        refreshGamePieces: function () {
            let ref = this;
            let parent = ref.$parent;

            if (this.gameUuid) {
                $.ajax({
                    url: `${parent.baseApi}/api/v1/game/pieces`,
                    type: "GET",
                    cache: false,
                    timeout: 30000,
                    data: `uuid=${this.gameUuid}`,
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader("Authorization", `Bearer ${parent.oauth}`);
                    },
                }).done(function (pieces) {
                    ref.updateBoardPieces(pieces);
                }).fail(function () {
                    alertify.error("Unable to fetch the pieces!", 5);
                });
            } else {
                alertify.error("Unable to fetch the pieces location (uuid is not available)!", 5);
            }
        },
        //---------------------------------------------------------------------------
        registerEvents: function () {
            let ref = this;
            let parent = ref.$parent;

            /**
             * Drag events
             */
            document.addEventListener("dragover", function (event) {
                event.preventDefault();
            });

            $(document).on("dragstart", ".board-pieces", function (event) {
                let dataTransfer = event.originalEvent.dataTransfer;
                dataTransfer.setData("from", $(event.target).parent().data('case-id'));
            });

            $(document).on("drop", ".bord-case", function (event) {
                ref.whenPieceDraggedEvent(event);
            });

            let $boardCaseWithPieceSelector = $(".bord-case > span.board-pieces");

            $boardCaseWithPieceSelector.mouseover(function () {

                if (!ref.gameUuid) {
                    return;
                }

                let from = $(this).parent().attr("data-case-id");

                $.ajax({
                    url: `${ref.$parent.baseApi}/api/v1/game/moves`,
                    type: "GET",
                    cache: false,
                    timeout: 30000,
                    data: `from=${from}&uuid=${ref.gameUuid}`,
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader("Authorization", `Bearer ${parent.oauth}`);
                    }
                }).fail(function () {
                    alertify.error("Unable to get the moves positions!", 5);
                });
            });

            $boardCaseWithPieceSelector.mouseleave(function () {
                $("div").removeClass("piece-available-moves");
            });
        },
        //---------------------------------------------------------------------------
        saveUuid: function (data) {
            this.gameUuid = data.response;
        },
        //---------------------------------------------------------------------------
        onGameEvent: function (payload) {
            let parsed = JSON.parse(payload.body);
            let chessEvent = parsed.event;
            let message = parsed.message;

            switch (chessEvent) {
                // case 'UI_SESSION_EXPIRED': //FIXME
                //     window.setInterval(function () {
                //         location.reload();
                //     }, 10 * 1000);
                //     alertify.error(message, 0);
                //     break;
                case 'PLAYER_JOINED':
                    alertify.success(message, 6);
                    break;
                case 'TRY_JOIN_GAME':
                    alertify.error(message, 0);
                    break;
                case 'MOVE':
                    this.refreshGamePieces();
                    this.eventLog.push(message);
                    break;
                case 'GAME_WON':
                    this.eventLog.push(message);
                    break;
                case 'GAME_WON_EVENT_MOVE':
                    this.eventLog.push(message);
                    break;
                case 'SCORE_UPDATE':
                    this.blackPlayerScore = message.blackPlayerPoint;
                    this.whitePlayerScore = message.whitePlayerPoint;
                    break;
                case 'REFRESH_BOARD':
                    this.refreshGamePieces();
                    break;
                case 'PAWN_PROMOTION':
                    alertify.warning(message);
                    break;
                case 'KING_CHECKMATE':
                    alertify.warning(message, 5);
                    break;
            }
        },
        //---------------------------------------------------------------------------
        onGameSideEvent: function (payload) {
            let parsed = JSON.parse(payload.body);
            let chessEvent = parsed.event;
            let message = parsed.message;
            let obj = parsed.obj;

            switch (chessEvent) {
                case 'PLAYER_TURN':
                    ref.eventLog.push(message);
                    break;
                case 'PAWN_PROMOTION':
                    //FIXME
                    break;
                case 'KING_CHECK':
                    alertify.warning(message);
                    break;
                case 'AVAILABLE_MOVE':
                    const from = obj.from;
                    if (from) {
                        $("div").removeClass("piece-available-moves"); //clear
                        var positions = obj.positions;
                        for (let i = 0; i < positions.length; i++) {
                            $(`[data-case-id='${positions[i]}']`).addClass("piece-available-moves");
                        }
                    }
                    break;

            }
        },
        //---------------------------------------------------------------------------
        initGameComponents: function () {
            let ref = this;
            let parent = ref.$parent;

            let stompClientRef = this.stompClient;
            if (stompClientRef) {
                stompClientRef.unsubscribe();
            } else {
                let sockJS = new SockJS(`/websocket?access_token=${parent.oauth}`, null, {transports: ['xhr-streaming']});
                stompClientRef = Stomp.over(sockJS);
                this.stompClient = stompClientRef;
            }

            let headers = {
                "Authorization": `Bearer ${parent.oauth}`
            };

            stompClientRef.connect(headers, function () {
                stompClientRef.subscribe(`/topic/${ref.gameUuid}`, ref.onGameEvent);
                stompClientRef.subscribe(`/topic/${ref.gameUuid}/${ref.gameSide}`, ref.onGameSideEvent);
            });
        },
        //---------------------------------------------------------------------------
        createNewGameWithProperties: function () {
            let ref = this;
            let parent = ref.$parent;

            $.ajax({
                url: `${this.$parent.baseApi}/api/v1/game/create`,
                type: "POST",
                cache: false,
                timeout: 30000,
                data: `side=${this.gameSide}&againstComputer=${this.againstComputer}&observers=${this.observers}`,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Authorization", `Bearer ${parent.oauth}`);
                }
            }).done(function (data) {
                ref.saveUuid(data);
                ref.refreshGamePieces();
                ref.initGameComponents();
                $('#new-game-modal').modal('hide')
            }).fail(function () {
                alertify.error("Unable to create a new game!", 5);
            });
        },
        //---------------------------------------------------------------------------
        newGame: function () {
            $('#new-game-modal').modal('toggle')
        },
        //---------------------------------------------------------------------------
        whenPieceDraggedEvent: function (event) {
            let ref = this;
            let dataTransfer = event.originalEvent.dataTransfer;
            let from = dataTransfer.getData("from");
            let to = $(event.target).data('case-id');

            if (this.gameUuid && from && to && (from !== to)) {
                $.ajax({
                    url: `${this.$parent.baseApi}/api/v1/game/move`,
                    type: "POST",
                    cache: false,
                    timeout: 30000,
                    data: `from=${from}&to=${to}&uuid=${ref.gameUuid}`,
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader("Authorization", `Bearer ${ref.$parent.oauth}`);
                    },
                }).fail(function () {
                    alertify.error("Unable to move to the selected position!", 5);
                });
            }
        }
    }
};