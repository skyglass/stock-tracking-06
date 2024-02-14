package net.greeta.stock.ordering.infrastructure.commandbus;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import lombok.RequiredArgsConstructor;
import net.greeta.stock.shared.eventhandling.RequestManager;
import net.greeta.stock.shared.eventhandling.commands.IdempotentCommandBus;
import net.greeta.stock.shared.eventhandling.commands.IdentifiedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Provides implementation for handling duplicate request and ensuring idempotent updates, in the cases where
 * a requestid sent by client is used to detect duplicate requests.
 */
@RequiredArgsConstructor
@Component
public class IdempotentCommandBusImpl implements IdempotentCommandBus {
  private static final Logger logger = LoggerFactory.getLogger(IdempotentCommandBusImpl.class);

  private final RequestManager requestManager;
  private final Pipeline pipeline;

  @Override
  public <R, C extends Command<R>> R send(C c) {
    if (c instanceof IdentifiedCommand<?, ?> identifiedCommand) {
      logger.info("Send identified {} command", c.getClass().getSimpleName());
      return handleIdentifiedCommand(identifiedCommand);
    } else {
      logger.info("Send {} command", c.getClass().getSimpleName());
      return pipeline.send(c);
    }
  }

  private <R> R handleIdentifiedCommand(IdentifiedCommand<?, ?> identifiedCommand) {
    if (requestManager.exist(identifiedCommand.getId())) {
      logger.info("The command with id {} is already handled", identifiedCommand.getId());
      return (R) pipeline.send(identifiedCommand);
    } else {
      var command = identifiedCommand.getCommand();
      requestManager.createRequestForCommand(identifiedCommand.getId(), command.getClass().getSimpleName());
      logger.info("Send command with id {}", identifiedCommand.getId());
      return (R) pipeline.send(command);
    }
  }

}