# Android Calculator App (Kotlin)

A modern **Android calculator app** built with **Kotlin**, featuring accurate expression parsing, operator precedence, parentheses support, percentages, and unary minus — without relying on built-in `eval()` methods.

This project focuses on **clean UI** and **correct mathematical evaluation** using classic parsing algorithms.

---

## 🚀 Features

- Basic arithmetic  
  `+  −  ×  ÷`
- Percentage (`%`) support
- Parentheses `( )`
- Unary minus (`+ / -`)
- Decimal numbers
- Backspace & clear (C)
- Previous expression preview
- Smart operator replacement
- Proper operator precedence
- Error handling for invalid expressions
- Clean dark UI with responsive buttons

---

## 🧠 How Calculation Works

The calculator evaluates expressions in **three stages**:

1. **Tokenization**
   - Splits the input into numbers, operators, and parentheses
   - Detects unary minus (`u-`) correctly

2. **Shunting Yard Algorithm**
   - Converts infix expressions into Reverse Polish Notation (RPN)
   - Guarantees correct precedence and associativity

3. **RPN Evaluation**
   - Uses a stack-based approach to compute the final result

This makes the logic reliable, extensible, and safe.

---

## ➗ Operator Precedence

| Operator | Description | Priority |
|--------|------------|----------|
| u- | Unary minus | Highest |
| × ÷ | Multiply / Divide | Medium |
| + − | Add / Subtract | Lowest |

---

## 🎨 UI Highlights

- Built with **ConstraintLayout**
- Square, responsive buttons
- Dark theme with accent colors
- Clear separation between:
  - Current result
  - Previous expression
- Material-style interaction feedback

---

## 🛠️ Tech Stack

- **Kotlin**
- **Android SDK**
- **ConstraintLayout**
- **Stack-based expression evaluation**
- **Shunting Yard Algorithm**

---

## ▶️ Running the App

1. Clone the repository:
   ```bash
   git clone https://github.com/YoussefMohamed44/android-calculator.git
