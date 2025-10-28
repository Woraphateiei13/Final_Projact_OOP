package event;

import character.*;

public class Event {
    public static boolean checkHit(Botnoi bot, Enemy_Bug enebug) {
        boolean x_Collision = (bot.x < enebug.x + enebug.width) && (bot.x + bot.botSize > enebug.x);
        boolean y_Collision = (bot.y < enebug.y + enebug.height) && (bot.y + bot.botSize > enebug.y);
        return x_Collision && y_Collision;

        // if (bot.x + bot.botSize > enebug.x && bot.x < enebug.x) {
        // if (bot.y + bot.botSize >= enebug.y - enebug.height) {
        // return true;
        // }
        // }
        // return false;
    }

    public static boolean checkHit(Botnoi bot, Enemy_Firewall enefire) {
        boolean x_Collision = (bot.x < enefire.x + enefire.width) && (bot.x + bot.botSize > enefire.x);
        boolean y_Collision = (bot.y < enefire.y + enefire.height) && (bot.y + bot.botSize > enefire.y);
        return x_Collision && y_Collision;
    }
}
