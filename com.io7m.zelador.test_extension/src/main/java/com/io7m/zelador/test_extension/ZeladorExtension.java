/*
 * Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package com.io7m.zelador.test_extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * The Zelador extension.
 */

public final class ZeladorExtension
  implements ParameterResolver,
  CloseableResourcesType,
  AfterAllCallback,
  AfterEachCallback
{
  private static final Logger LOG =
    LoggerFactory.getLogger(ZeladorExtension.class);

  private final ConcurrentLinkedDeque<AutoCloseable> perTestResources;
  private final ConcurrentLinkedDeque<AutoCloseable> perTestClassResources;

  /**
   * The Zelador extension.
   */

  public ZeladorExtension()
  {
    this.perTestResources =
      new ConcurrentLinkedDeque<>();
    this.perTestClassResources =
      new ConcurrentLinkedDeque<>();
  }

  @Override
  public boolean supportsParameter(
    final ParameterContext parameterContext,
    final ExtensionContext extensionContext)
    throws ParameterResolutionException
  {
    final var requiredType =
      parameterContext.getParameter().getType();
    return Objects.equals(requiredType, CloseableResourcesType.class);
  }

  @Override
  public Object resolveParameter(
    final ParameterContext parameterContext,
    final ExtensionContext extensionContext)
    throws ParameterResolutionException
  {
    final var requiredType =
      parameterContext.getParameter().getType();

    if (Objects.equals(requiredType, CloseableResourcesType.class)) {
      return this;
    }

    throw new ParameterResolutionException(
      "Unrecognized requested parameter type: %s".formatted(requiredType)
    );
  }

  @Override
  public <T extends AutoCloseable> T addPerTestResource(
    final T x)
  {
    this.perTestResources.push(x);
    return x;
  }

  @Override
  public <T extends AutoCloseable> T addPerTestClassResource(
    final T x)
  {
    this.perTestClassResources.push(x);
    return x;
  }

  @Override
  public void afterAll(
    final ExtensionContext context)
  {
    while (!this.perTestClassResources.isEmpty()) {
      final var x = this.perTestClassResources.pop();
      try {
        x.close();
      } catch (final Exception e) {
        LOG.error("Failed to close {}: ", x);
      }
    }
  }

  @Override
  public void afterEach(
    final ExtensionContext context)
  {
    while (!this.perTestResources.isEmpty()) {
      final var x = this.perTestResources.pop();
      try {
        x.close();
      } catch (final Exception e) {
        LOG.error("Failed to close {}: ", x);
      }
    }
  }
}
