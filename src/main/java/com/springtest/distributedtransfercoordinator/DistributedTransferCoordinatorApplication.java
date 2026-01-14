package com.springtest.distributedtransfercoordinator;

import com.springtest.distributedtransfercoordinator.core.commands.CLICommands;
import com.springtest.distributedtransfercoordinator.services.TransferService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class DistributedTransferCoordinatorApplication {
    final TransferService transferService;

    public DistributedTransferCoordinatorApplication(TransferService transferService) {
        this.transferService = transferService;
    }

    static void main(String[] args) {
        SpringApplication.run(DistributedTransferCoordinatorApplication.class, args);
    }

    @Bean
    CommandLineRunner run() {
        return args -> {
            CLICommands.WelcomeMessage();
            while (true) {
                CLICommands.ShowOptions();
                var input = CLICommands.getInput();

                switch (input) {
                    case "1":
                        var escrows = transferService.getEscrows();
                        CLICommands.showEscrows(escrows);
                        break;
                    case "2":
                        var sellers = transferService.getSellers();
                        CLICommands.showSellers(sellers);
                        break;
                    case "3":
                        System.out.println("> Which escrow record do you wish to transfer?");
                        CLICommands.showEscrows(transferService.getEscrows());

                        // Map input to escrow
                        var selectedEscrow = CLICommands.getInput();
                        System.out.println("> Now which seller should receive the transfer amount?");

                        CLICommands.showSellers(transferService.getSellers());
                        var selectedSeller = CLICommands.getInput();

                    default:
                        System.out.println("> Invalid selection, try again.");
                        break;
                }
            }
        };
    }
}
