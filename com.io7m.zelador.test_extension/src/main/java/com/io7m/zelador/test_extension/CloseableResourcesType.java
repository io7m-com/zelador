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

/**
 * An interface that will clean up closeable resources on behalf of tests.
 */

public interface CloseableResourcesType
{
  /**
   * Add a resource that will be closed when the current test finishes
   * executing.
   *
   * @param x   The resource
   * @param <T> The precise type of resource
   *
   * @return The resource
   */

  <T extends AutoCloseable> T addPerTestResource(T x);

  /**
   * Add a resource that will be closed when all the current tests finish
   * executing.
   *
   * @param x   The resource
   * @param <T> The precise type of resource
   *
   * @return The resource
   */

  <T extends AutoCloseable> T addPerTestClassResource(T x);
}
