package com.illtamer.infinite.bot.minecraft;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        int[] cells = new int[n];
        for (int i = 0; i < n; i++) {
            cells[i] = scanner.nextInt();
        }

        int minDrops = 0;
        for (int i = 0; i < n; i++) {
            if (cells[i] >= 6) {
                int extraDrops = cells[i] - 5;
                minDrops += extraDrops;

                if (i > 0) cells[i - 1] += extraDrops;
                if (i < n - 1) cells[i + 1] += extraDrops;
                cells[i] = 5;
            }
        }
        System.out.println(minDrops);
    }

}
