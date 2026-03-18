The Parser's Blueprint (For the Dev Team)
To make this forgiving, plain-text experience work for the user, our pure Java Lexer and Recursive Descent Parser must implement the following specific behavioral rules.

1. Tokenizing Assignments
The Lexer must strictly treat = and : as the exact same token type (e.g., TokenType.ASSIGNMENT_OPERATOR). This allows the Parser's logic to remain simple while giving the user flexibility.

2. Lexer States (Context-Aware Reading)
To allow users to type freely without quotation marks, the Lexer must use two distinct internal states:

INITIAL State: The Lexer is looking for Keys (e.g., Name, Section, SubSection) and Operators (=, :).

READ_VALUE State: Triggered the moment the Lexer matches an ASSIGNMENT_OPERATOR. In this state, the Lexer stops caring about special keywords or internal punctuation. It absorbs every character it sees into a single STRING_VALUE token.

3. The Lookahead Rule (Handling Multi-line Indentation)
When the Lexer is in the READ_VALUE state and encounters a newline character (\n), it must not immediately end the token. Instead, it must "peek" at the next character:

If the next character is whitespace (Tab or Space): The Lexer replaces the \n with a space, remains in the READ_VALUE state, and continues appending text to the current string.

If the next character is an alphanumeric letter: The Lexer realizes the value is finished. It saves the STRING_VALUE token, exits READ_VALUE mode, and switches back to the INITIAL state to hunt for the next Key.

4. Detecting Lists and Bullet Points
Bullet points require a specific tokenization path so the Parser can group them into Java Lists/Arrays.

If the Lexer detects a newline (\n), followed by indentation (\t or spaces), followed by a hyphen (-), it must trigger a BULLET_ITEM token.

The Lexer then reads the rest of that line as the content of the bullet point, stopping at the next newline.