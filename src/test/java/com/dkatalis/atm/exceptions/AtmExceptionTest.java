package com.dkatalis.atm.exceptions;

import org.testng.annotations.Test;
import com.dkatalis.atm.AtmContext;
import com.dkatalis.atm.exceptions.AtmException;
import com.dkatalis.atm.exceptions.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Oleg Cherednik
 * @since 10.08.2021
 */
@Test
public class AtmExceptionTest {

    public void shouldInvokeErrorCodeGetMessageWhenGetMessage() {
        AtmException e = new AtmException(ErrorCode.EMPTY_INPUT);
        AtmContext context = mock(AtmContext.class);
        String str = e.getMessage(context);
        assertThat(str).isEqualTo(ErrorCode.EMPTY_INPUT.getMessage(context));
        verifyNoMoreInteractions(context);
    }

}
