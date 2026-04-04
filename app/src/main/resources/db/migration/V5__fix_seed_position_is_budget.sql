-- Fix seed positions: mark all as non-budget so they are visible
-- to the position preview endpoint on the submission create page.
-- Budget positions are a separate concept (headcount plan lines);
-- these are actual job positions that submissions reference.
UPDATE position SET is_budget = FALSE
WHERE number IN ('DATA001', 'DATA002', 'BE001', 'FE001');
