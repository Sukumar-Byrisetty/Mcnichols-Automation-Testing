# Online Ordering Removal: Safe Change Plan

## Objective

Stabilize Development regression tests after Online Ordering page removal, with minimal, reviewable changes that do not alter core business logic.

## Scope and Constraints

Scope: Development order-flow tests that currently depend on OnlineOrderingProductListingPage.
Constraint: Do not modify page object business rules, checkout logic, credential logic, or utility behavior unless strictly required.
Strategy: Replace deleted-page navigation with existing search-driven navigation already used elsewhere in test suite.

## Files Proposed For Change (Only)

### 1) src/test/java/com/mcnichols/tests/PlaceOrderForRegisteredUser.java

Why this file:
This test still imports and uses OnlineOrderingProductListingPage.
It contains direct navigation to the removed page and item selection from that page.

Planned edits:
Remove direct dependency on removed PLP class.
In canGoToOnlineOrderingPage(): replace removed-page navigation with on-site search using TestingConfig.getItemForOnlinePurchase().
In canGoToProductPage(): keep product-page and purchasable-SKU assertions; remove explicit PLP item click path.
Keep fallback behavior, but make fallback use a second search for a purchasable item instead of returning to removed PLP.

Why this is safe:
Only test navigation entry point changes.
Existing checkout assertions and test intent remain unchanged.
Uses existing framework API already used by other tests.

### 2) src/test/java/com/mcnichols/tests/ExpressCheckoutOrder.java

Why this file:
Also imports and navigates through OnlineOrderingProductListingPage.
Likely fails at same removed-page boundary.

Planned edits:
Remove direct dependency on removed PLP class.
In canGoToOnlineOrderingPage(): replace removed-page navigation with search for online-purchasable item via TestingConfig.getItemForOnlinePurchase().
In canGoToProductPage(): keep current product page and purchasable checks, remove PLP item index click.

Why this is safe:
Leaves express checkout business flow intact.
Changes are localized to pre-cart navigation setup only.

## Files Explicitly Not Changing

src/main/java/com/mcnichols/framework/pages/**(all page objects)
src/main/java/com/mcnichols/framework/config/** (config model/selection logic)
src/main/java/com/mcnichols/framework/util/** (framework utilities)
src/test/java/com/mcnichols/tests/PlaceQuoteForRegisteredUser.java (already search-based)
pom.xml and TestNG suite XML files (unless requested in a separate review)

## Optional but Separate (Approval Required)

### 3) src/main/resources/framework/util/test-base-config.xml

Optional change:
Set use.headless.mode to false for easier visible debugging.

Reason to keep separate:
Execution-mode change affects all runs, not just these two tests.
Better to approve independently from functional navigation fix.

## Non-Goals

No refactor of test architecture.
No changing business assertions (pricing, shipping, checkout validations).
No updates to production package tests in this pass.

## Risk Assessment

Primary risk: Search may land on product variants that are not immediately purchasable.
Mitigation: Keep existing SKU purchasable checks and one controlled retry.
Containment: Limit edits to two Development test classes.

## Validation Plan (After Approval)

1. Compile check:
   - mvn -DskipTests compile
2. Run only Development Place Order suite:
   - mvn test -Prun "-DrunSuite=src/test/resources/development/place-order-test.xml"
3. If green, run Development Quote suite (sanity, expected unchanged):
   - mvn test -Prun -DrunSuite=src/test/resources/development/place-quote-test.xml
4. Run full Development profile:
   - mvn test -U -Pdevelopment-regression-tests
5. Review outputs:
   - target/surefire-reports
   - test-report/<latest-run>/

## Rollback Plan

Revert only these two test files if needed:

- src/test/java/com/mcnichols/tests/PlaceOrderForRegisteredUser.java
- src/test/java/com/mcnichols/tests/ExpressCheckoutOrder.java

## Review Checklist

[ ] Only 2 test classes modified
[ ] No changes in framework/page object code
[ ] No changes in production test package
[ ] Existing checkout assertions preserved
[ ] Development suites pass in headed mode (if enabled)

## Approval Gate

Proceed only after explicit approval on:
File list
Navigation replacement approach (search-based)
Whether to include global headed-mode config change in same PR
