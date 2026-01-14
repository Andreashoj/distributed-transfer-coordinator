package com.springtest.distributedtransfercoordinator.core.commands;

import com.springtest.distributedtransfercoordinator.db.escrow.models.Escrow;
import com.springtest.distributedtransfercoordinator.db.seller.models.Seller;

import java.util.List;
import java.util.Scanner;

public class CLICommands {
    public static void WelcomeMessage() {
        System.out.println("> Welcome dear sir");
        System.out.println("> You are in control of transfers between the escrow account and the seller account.");
        System.out.println("> These are your actionable options:");
    }

    public static void ShowOptions() {
        System.out.println("> 1: List pending escrow instances");
        System.out.println("> 2: List sellers");
        System.out.println("> 3: Try and complete transfer between escrow and seller");
    }

    public static String getInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public static void showEscrows(List<Escrow> escrows) {
        for (var i = 0; i < escrows.size(); i++) {
            var escrow = escrows.get(i);
            System.out.printf("> %d: ID: %s ==== STATUS: %s ==== AMOUNT: %s$%n",
                    i + 1,
                    escrow.getId()
                            .toString(),
                    escrow.getStatus(),
                    escrow.getAmount()
            );
        }
    }

    public static void showSellers(List<Seller> sellers) {
        for (var i = 0; i < sellers.size(); i++) {
            System.out.printf("> %d: ID: %s ==== BALANCE: %s$%n", i + 1, sellers.get(i)
                            .getId()
                            .toString(),
                    sellers.get(i)
                            .getBalance());
        }
    }
}
