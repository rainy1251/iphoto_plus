package com.iphoto.plus.event;

public class ChangeCardEvent {

    public boolean isChangeCard() {
        return isChangeCard;
    }

    public void setChangeCard(boolean changeCard) {
        isChangeCard = changeCard;
    }

    public ChangeCardEvent(boolean isChangeCard) {
        this.isChangeCard = isChangeCard;
    }

    public boolean isChangeCard;


}
