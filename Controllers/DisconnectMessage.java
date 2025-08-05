package controller;

import java.io.Serializable;

public class DisconnectMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private String clubName;

    public DisconnectMessage(String clubName) {
        this.clubName = clubName;
    }



    public String getClubName() {
        return clubName;
    }


}

