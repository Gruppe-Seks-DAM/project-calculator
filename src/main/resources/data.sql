INSERT INTO project (name, description, deadline) VALUES
    ('Renovering i København',
     'Renoveringsprojekt for 3 boliger i København.',
     DATE '2025-12-17');

INSERT INTO subproject (project_id, name, description, deadline) VALUES
    (1, 'Lille Langgade 8, 2. tv', 'Renovering 1: Lejlighed på Nyhavn', DATE '2025-12-01'),
    (1, 'Strandgade 112', 'Renovering 2: Hus ved Amager Strand', DATE '2025-12-11');

-- UPDATED: Add estimated_hours to task inserts
INSERT INTO task (subproject_id, name, description, deadline, estimated_hours) VALUES
 (1, 'Fjernelse af gulv', 'Fjerne gammelt gulv og affald', DATE '2025-11-20', 8.0),
 (1, 'Ny el-installation', 'Opgradere ledninger', DATE '2025-11-25', 12.5),
 (2, 'Facadeafrensning', 'Rense og reparere facade', DATE '2025-12-05', 20.0);

-- Keep subtask inserts the same (already have estimated_hours)
INSERT INTO subtask (task_id, name, description, deadline, estimated_hours) VALUES
    (1, 'Rive gulv', 'Rive og bortskaffe gulv', DATE '2025-11-18', 6.0),
    (1, 'Sortere materialer', 'Sortere genbrug/affald', DATE '2025-11-19', 2.0),
    (2, 'Installere eltavle', 'Montere ny tavle og sikringer', DATE '2025-11-24', 8.0),
    (3, 'Trykspule facade', 'Rense facade', DATE '2025-12-02', 4.5);