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


package com.io7m.zelador.tests;

import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ZeladorExtension.class)
public final class ZExtensionTest
{
  private static final ConcurrentHashMap.KeySetView<CloseThing, Boolean> ALL_RESOURCES =
    ConcurrentHashMap.newKeySet();

  private static final class CloseThing implements AutoCloseable
  {
    @Override
    public String toString()
    {
      return "[CloseThing %d]".formatted(Integer.valueOf(this.index));
    }

    private final int index;
    private volatile boolean closed;
    private volatile boolean crash;

    private CloseThing(
      final int index)
    {
      this.closed = false;
      this.crash = false;
      this.index = index;
    }

    @Override
    public void close()
      throws Exception
    {
      this.closed = true;
      if (this.crash) {
        throw new IOException("Failed!");
      }
    }
  }

  @Test
  public void test0(
    final CloseableResourcesType closeables)
  {
    ALL_RESOURCES.add(closeables.addPerTestResource(new CloseThing(0)));
  }

  @Test
  public void test1(
    final CloseableResourcesType closeables)
  {
    ALL_RESOURCES.add(closeables.addPerTestClassResource(new CloseThing(1)));
  }

  @Test
  public void test2(
    final CloseableResourcesType closeables)
  {
    final var x = new CloseThing(2);
    x.crash = true;
    ALL_RESOURCES.add(closeables.addPerTestClassResource(x));
  }

  @Test
  public void test3(
    final CloseableResourcesType closeables)
  {
    final var thing = new CloseThing(3);
    thing.crash = true;

    ALL_RESOURCES.add(closeables.addPerTestResource(thing));
  }

  @Test
  public void test4(
    final CloseableResourcesType closeables)
  {
    ALL_RESOURCES.add(closeables.addPerTestResource(new CloseThing(4)));
  }

  @AfterAll
  public static void testAll()
  {
    assertEquals(5, ALL_RESOURCES.size());

    for (final var x : ALL_RESOURCES) {
      if (x.index == 1 || x.index == 2) {
        continue;
      }
      assertTrue(x.closed, x + " is closed.");
    }
  }
}
